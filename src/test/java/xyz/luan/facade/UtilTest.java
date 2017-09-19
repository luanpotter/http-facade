package xyz.luan.facade;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UtilTest {

    @Test
    public void extractTest() {
        String text = "this is my id: 12342! don't lose it";
        String needle = Util.extract("[^\\d]*([\\d]*)[^\\d]*", text);
        assertEquals("12342", needle);
    }
}
