package org.dynjs.spec;

import java.io.File;

public class TestGenerator {
    private final static String FILE_TEMPLATE = "package org.dynjs.spec.$SECTION;";
    private final static String TEST_TEMPLATE = "";
    private final static String SUITE_DIRECTORY = "test/resources/suite";

    public static void main(String[] args){
        File suiteDirectory = new File(SUITE_DIRECTORY);

        if(!suiteDirectory.exists()) {
            System.out.println("Creating " + suiteDirectory);
            suiteDirectory.mkdir();
        }

        traverse(new File(SUITE_DIRECTORY));
    }

    private static void traverse(File parent) {
        if(parent.isDirectory()){
            for(File file : parent.listFiles()){
                traverse(file);
            }
        } else if(parent.getName().endsWith(".js")) {
            generate(parent.getName());
        }
    }

    private static void generate(String fileName) {
        System.out.println("Generate stuff to " + fileName);
        File testFile = new File(SUITE_DIRECTORY + "/" + fileName);
    }
}