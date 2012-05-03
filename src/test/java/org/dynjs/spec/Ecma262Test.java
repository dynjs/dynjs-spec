package org.dynjs.spec;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.*;
import org.junit.runner.RunWith;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RunWith(DynJSTestRunner.class)
public class Ecma262Test {

    private static final List<String> BLACKLIST = Collections.emptyList();

    public static Collection<File> files() throws URISyntaxException {
        final URL resource = Ecma262Test.class.getResource("/suite");
        if (resource.getProtocol().equals("file")) {
            return FileUtils.listFiles(new File(resource.toURI()),
                    new AndFileFilter(new SuffixFileFilter("js"),
                            new NotFileFilter(new NameFileFilter(BLACKLIST))),
                    TrueFileFilter.INSTANCE);
//            return FileUtils.listFiles(new File(resource.toURI()), new String[]{"js"}, true);
        }
        throw new RuntimeException("failed loading test suite");
    }

    public static Collection<File> filesToPreload() throws URISyntaxException {
        return Arrays.asList(new File(Ecma262Test.class.getResource("/harness/cth.js").toURI())
                //new File(Ecma262Test.class.getResource("/harness/ed.js").toURI()),
//                new File(Ecma262Test.class.getResource("/harness/framework.js").toURI())
        );
    }

}
