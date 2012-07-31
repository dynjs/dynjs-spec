package org.dynjs.spec.runner;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class JSFileUtils {
    private static final List<String> BLACKLIST = Collections.emptyList();

    public static Collection<File> listFiles(String folderName) throws URISyntaxException {
        final URL resource = JSFileUtils.class.getResource("/suite");
        if (resource.getProtocol().equals("file")) {
            File suiteDir = new File(resource.toURI());
            File folder = new File(suiteDir, folderName);
            if (!folder.exists()) {
                throw new RuntimeException(String.format(
                        "Folder %s doesn't exist in /suite.", folderName));
            }

            return FileUtils.listFiles(folder, new AndFileFilter(
                    new SuffixFileFilter("js"), new NotFileFilter(
                    new NameFileFilter(BLACKLIST))),
                    TrueFileFilter.INSTANCE);
        }

        throw new RuntimeException("failed loading test suite");
    }

    public static Collection<File> listPreloadFiles() throws URISyntaxException {
        return Arrays.asList(
                new File(JSFileUtils.class.getResource("/harness/cth.js")
                        .toURI()),
                new File(JSFileUtils.class.getResource("/harness/sta-lite.js")
                        .toURI()));
    }

}
