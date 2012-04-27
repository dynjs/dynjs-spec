package org.dynjs.spec;

import org.apache.commons.io.FileUtils;
import org.dynjs.runtime.DynJS;
import org.dynjs.runtime.DynJSConfig;
import org.dynjs.runtime.DynThreadContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@RunWith(Parameterized.class)
public class Ecma262Test {

    private final File file;

    public Ecma262Test(File file) {
        this.file = file;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> files() throws URISyntaxException {
        List<Object[]> list = new LinkedList<Object[]>();

        final URL resource = Ecma262Test.class.getResource("/suite");
        if (resource.getProtocol().equals("file")) {
            Collection<File> files = FileUtils.listFiles(new File(resource.toURI()), new String[]{"js"}, true);
            for (File file : files) {
                list.add(new Object[]{file});
            }
        }
        return list;
    }

    @Test
    public void ecma262Tests() throws IOException {
        DynJS dynJS = new DynJS(new DynJSConfig());
        DynThreadContext context = new DynThreadContext();
        FileInputStream is = new FileInputStream(this.file);
        dynJS.eval(context, is);
        is.close();
    }
}
