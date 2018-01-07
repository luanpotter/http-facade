package xyz.luan.facade;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UrlEncodeTest {

    @Test
    public void testEncode() {
        assertEquals("a%3Ab", Util.urlEncodeUTF8("a:b"));
    }

    @Test
    public void testDecode() {
        assertEquals("a%b", Util.urlDecodeUTF8("a%25b"));
    }

    @Test
    public void testEncodeSpace() {
        assertEquals("a+b", Util.urlEncodeUTF8("a b"));
        assertEquals("a b", Util.urlDecodeUTF8("a+b"));
        assertEquals("a b", Util.urlDecodeUTF8("a%20b"));
    }
}
