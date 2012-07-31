package org.dynjs.spec.runner;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class JSFileUtilsTest {
    @Test
    public void testListFiles() throws Exception {
        assertThat(JSFileUtils.listFiles("ch15")).hasSize(8071);
        assertThat(JSFileUtils.listFiles("ch15/15.3/15.3.2/15.3.2.1")).hasSize(14);
    }
}
