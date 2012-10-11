/**
 *  Copyright 2012 Douglas Campos, and individual contributors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.dynjs.spec.runner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;

import org.dynjs.Config;
import org.dynjs.runtime.DynJS;
import org.dynjs.runtime.GlobalObject;
import org.dynjs.runtime.GlobalObjectFactory;
import org.dynjs.spec.shims.FailShim;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

public class DynJSTestRunner extends Runner implements Filterable {

    public static final int TIMEOUT_IN_SECONDS = 15;
    private final Class<?> testClass;
    private Collection<File> files = new ArrayList<>();
    private Collection<File> filesToPreload;

    private static List<String> SKIP_TESTS = new ArrayList<String>() {
        {
            add("S15.4.5.2_A3_T4.js");
            add("S15.1.3.1_A2.5_T1.js");
        }
    };

    public DynJSTestRunner(Class<?> testClass) {
        this.testClass = testClass;
        init();

    }

    @Override
    public Description getDescription() {
        final Description description = Description.createSuiteDescription(testClass);
        for (File file : files) {
            if (!SKIP_TESTS.contains(file.getName())) {
                Description child = Description.createTestDescription(testClass, file.getName());
                description.addChild(child);
            }
        }
        return description;
    }

    private void init() {
        try {
            Constructor<?> constructor = testClass.getConstructor(null);
            Object folderRunner = constructor.newInstance();
            Method getFiles = testClass.getMethod("files", null);
            this.files = (Collection<File>) getFiles.invoke(folderRunner, null);
            this.filesToPreload = JSFileUtils.listPreloadFiles();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run(RunNotifier notifier) {
        final ExecutorService service = Executors.newSingleThreadExecutor();
        for (final File file : files) {
            final Description description = Description.createTestDescription(testClass, file.getName());
            notifier.fireTestStarted(description);
            FileInputStream testFile = null;
            if (SKIP_TESTS.contains(file.getName())) {
                notifier.fireTestIgnored(description);
            } else {
                try {
                    TestDescriptor descriptor = getTestDescriptor(file);
                    try {
                        final DynJS dynJS = createDynJSRuntime();
                        try {
                            for (File fileToPreload : filesToPreload) {
                                FileInputStream stream = new FileInputStream(fileToPreload);
                                dynJS.execute(stream, file.getName());
                                stream.close();
                            }
                        } catch (Exception e) {
                            notifier.fireTestFailure(new Failure(description, e));
                            descriptor.passed = false;
                            descriptor.error = e;
                            continue;
                        }
                        testFile = new FileInputStream(file);
                        System.err.print(String.format("%-25s", file.getName()) + " | ");
                        final Future<Object> future = service.submit(new TestTask(dynJS, testFile, file, descriptor.onlyStrict));
                        try {
                            future.get(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
                        } catch (TimeoutException e) {
                            future.cancel(true);
                            throw e;
                        }
                        if ( descriptor.isNegative ) {
                            notifier.fireTestFailure(new Failure( description, new AssertionError("should have thrown: " + descriptor.negativeExpectation) ) );
                        } else {
                        notifier.fireTestFinished(description);
                        }
                        descriptor.passed = true;
                    } catch (Throwable e) {
                        if (!descriptor.isNegative) {
                            notifier.fireTestFailure(new Failure(description, e));
                            descriptor.passed = false;
                            descriptor.error = e;
                        } else {
                            if (descriptor.negativeExpectation == null) {
                                notifier.fireTestFinished(description);
                                descriptor.passed = true;
                            } else {
                                String msg = e.getMessage();
                                if (Pattern.matches(".*" + descriptor.negativeExpectation + ".*", msg)) {
                                    notifier.fireTestFinished(description);
                                    descriptor.passed = true;
                                } else {
                                    // System.err.println(e.getMessage());
                                    notifier.fireTestFailure(new Failure(description, e));
                                    descriptor.passed = false;
                                    descriptor.error = e;
                                }
                            }
                        }
                    } finally {
                        boolean wasTimeout = (descriptor.passed == false && (descriptor.error instanceof TimeoutException));
                        System.err.println((descriptor.passed ? "PASS" : (wasTimeout ? "TIME" : "FAIL")) + " | " + (descriptor.passed ? "" : descriptor.description));
                        if (!descriptor.passed && descriptor.error != null && !wasTimeout) {
                            System.err.println("    " + descriptor.error.getMessage());
                        }
                        try {
                            if (testFile != null) {
                                testFile.close();
                            }
                        } catch (Throwable e) {
                            notifier.fireTestFailure(new Failure(description, e));
                            descriptor.passed = false;
                            descriptor.error = e;
                        }
                    }
                } catch (IOException ioe) {
                    notifier.fireTestFailure(new Failure(description, ioe));
                }
            }
        }
        service.shutdown();
    }

    private TestDescriptor getTestDescriptor(File file) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        TestDescriptor descriptor = new TestDescriptor();
        try {
            String line = null;

            while ((line = in.readLine()) != null) {
                int negLoc = line.indexOf("@negative");
                if (negLoc >= 0) {
                    descriptor.isNegative = true;
                    String expectation = line.substring(negLoc + 9).trim();
                    if (!expectation.equals("")) {
                        descriptor.negativeExpectation = expectation;
                    }
                }
                int onlyStrict = line.indexOf("@onlyStrict");
                if (onlyStrict >= 0) {
                    descriptor.onlyStrict = true;
                }

                int noStrict = line.indexOf("@noStrict");
                if (noStrict >= 0) {
                    descriptor.noStrict = true;
                }

                int description = line.indexOf("@description");
                if (description >= 0) {
                    descriptor.description = line.substring(description + 12).trim();
                }
            }
        } finally {
            in.close();
        }

        return descriptor;

    }

    private static class TestDescriptor {
        public String description = null;
        public boolean onlyStrict = false;
        public boolean noStrict = false;
        public boolean isNegative = false;
        public String negativeExpectation = null;
        public boolean passed = false;
        public Throwable error = null;
    }

    private DynJS createDynJSRuntime() {
        Config config = new Config();
        config.setTimeZone( TimeZone.getTimeZone( "America/Los_Angeles"));
        config.setGlobalObjectFactory(new GlobalObjectFactory() {
            @Override
            public GlobalObject newGlobalObject(DynJS runtime) {
                final GlobalObject global = new GlobalObject(runtime);
                global.defineGlobalProperty("$$$fail", new FailShim(global));
                return global;
            }
        });
        return new DynJS(config);
    }

    @Override
    public void filter(Filter filter) throws NoTestsRemainException {
        Iterator<File> fileIter = this.files.iterator();

        while (fileIter.hasNext()) {
            File file = fileIter.next();
            Description child = Description.createTestDescription(testClass, file.getName());
            if (!filter.shouldRun(child)) {
                fileIter.remove();
            }
        }
    }

    private static class TestTask implements Callable<Object> {
        private final DynJS dynJS;
        private final FileInputStream testFile;
        private final File file;
        private boolean forceStrict;

        public TestTask(DynJS dynJS, FileInputStream testFile, File file, boolean forceStrict) {
            this.dynJS = dynJS;
            this.testFile = testFile;
            this.file = file;
            this.forceStrict = forceStrict;
        }

        @Override
        public Object call() throws Exception {
            dynJS.execute(testFile, file.getName(), forceStrict);
            return null;
        }
    }
}
