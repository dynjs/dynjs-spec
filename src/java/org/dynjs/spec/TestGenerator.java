package org.dynjs.spec;

import java.io.*;

public class TestGenerator {
    private final static String TEST_DIRECTORY = "test/java/org/dynjs/spec/";
    private final static String SUITE_DIRECTORY = "test/resources/suite/";

    public static void main(String[] args) {
        new TestGenerator().traverse(new File(SUITE_DIRECTORY));
    }

    private void traverse(File parent) {
        if (parent.isDirectory()) {
            for (File file : parent.listFiles()) {
                traverse(file);
            }
        } else if (parent.getName().endsWith(".js")) {
            generate(parent);
        }
    }

    private void generate(File specFile) {
        String chapter = specFile.getParent().replace(SUITE_DIRECTORY, "").split("/")[0];
        String parentDir = TEST_DIRECTORY + chapter;

        File testFile = new File(parentDir +
                "/" +
                specFile
                        .getName()
                        .replaceFirst("^\\d", "ch$0")
                        .replaceAll("\\.", "_")
                        .replaceFirst("_js$", "_Test.java"));

        if(!testFile.getParentFile().exists()){
            testFile.getParentFile().mkdirs();
        }

        try {
            OutputStream out = new DataOutputStream(new FileOutputStream(testFile));

            out.write(("package " + parentDir.replaceAll("test/java/", "").replaceAll("/", ".") + ";\n\n").getBytes());
            out.write(("public class " + testFile.getName().replaceFirst("\\.java", "") + " {\n\n}").getBytes());


            out.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


    }
}