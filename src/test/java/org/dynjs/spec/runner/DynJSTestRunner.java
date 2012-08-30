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

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

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

    private final Class<?> testClass;
    private Collection<File> files = new ArrayList<>();
    private Collection<File> filesToPreload;

    public DynJSTestRunner(Class<?> testClass) {
        this.testClass = testClass;
        init();

    }

    @Override
    public Description getDescription() {
        System.err.println("getDesc");
        final Description description = Description.createSuiteDescription(testClass);
        for (File file : files) {
            Description child = Description.createTestDescription(testClass, file.getName());
            description.addChild(child);
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
        for (File file : files) {
            final Description description = Description.createTestDescription(testClass, file.getName());
            notifier.fireTestStarted(description);
            FileInputStream testFile = null;
            try {

                DynJS dynJS = createDynJSRuntime();
                try {
                    for (File fileToPreload : filesToPreload) {
                        FileInputStream stream = new FileInputStream(fileToPreload);
                        dynJS.execute(stream, file.getName());
                        stream.close();
                    }
                } catch (Exception e) {
                    notifier.fireTestFailure(new Failure(description, e));
                    continue;
                }
                testFile = new FileInputStream(file);
                System.err.println(">>>> " + file.getName());
                dynJS.execute(testFile, file.getName());
                notifier.fireTestFinished(description);
            } catch (Throwable e) {
                System.err.println(e.getMessage());
                notifier.fireTestFailure(new Failure(description, e));
            } finally {
                System.err.println("<<<< " + file.getName());
                try {
                    if (testFile != null) {
                        testFile.close();
                    }
                } catch (Throwable e) {
                    notifier.fireTestFailure(new Failure(description, e));
                }
            }
        }
    }

    private DynJS createDynJSRuntime() {
        Config config = new Config();
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
        // System.err.println( "filtering with : " + filter.describe() );
        Iterator<File> fileIter = this.files.iterator();
        
        while ( fileIter.hasNext() ) {
            File file = fileIter.next();
            Description child = Description.createTestDescription(testClass, file.getName());
            if ( ! filter.shouldRun( child ) ) {
                fileIter.remove();
            }
        }
    }
}
