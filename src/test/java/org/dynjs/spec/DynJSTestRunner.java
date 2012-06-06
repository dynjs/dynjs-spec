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
package org.dynjs.spec;

import org.dynjs.runtime.DynJS;
import org.dynjs.runtime.DynJSConfig;
import org.dynjs.runtime.DynThreadContext;
import org.dynjs.spec.shims.FailShim;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

public class DynJSTestRunner extends Runner {

    private DynJS dynJS;

    private final Class<?> testClass;
    private Collection<File> files = new ArrayList<>();
    private Collection<File> filesToPreload;

    public DynJSTestRunner(Class<?> testClass) {
        this.testClass = testClass;
        init();
        DynJSConfig config = new DynJSConfig();
        config.addBuiltin("$$$fail", new FailShim());
        dynJS = new DynJS(config);
    }

    @Override
    public Description getDescription() {
        final Description description = Description.createSuiteDescription(testClass);
        for (File file : files) {
            description.addChild(Description.createTestDescription(testClass, file.getName()));
        }
        return description;
    }

    private void init() {
        try {
            Method getFiles = testClass.getDeclaredMethod("files");
            this.files = (Collection<File>) getFiles.invoke(null);
            Method getFilesToPreload = testClass.getDeclaredMethod("filesToPreload");
            this.filesToPreload = (Collection<File>) getFilesToPreload.invoke(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run(RunNotifier notifier) {
        for (File file : files) {
            final Description description = Description.createTestDescription(testClass, file.getName());
            notifier.fireTestStarted(description);
            final Boolean result;
            InputStream testFile = null;
            try {
                DynThreadContext context = new DynThreadContext();
                try {
                    for (File fileToPreload : filesToPreload) {
                        FileInputStream stream = new FileInputStream(fileToPreload);
                        dynJS.eval(context, stream, file.getName());
                        stream.close();
                    }
                } catch (Exception e) {
                    notifier.fireTestFailure(new Failure(description, e));
                    continue;
                }
                testFile = new FileInputStream(file);
                dynJS.eval(context, testFile);
                notifier.fireTestFinished(description);
            } catch (Throwable e) {
                notifier.fireTestFailure(new Failure(description, e));
            } finally {
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

}
