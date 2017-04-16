package xyz.luan.facade;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

    private Util() {
        throw new RuntimeException("Should never be instantiated");
    }

    public static String toString(InputStream stream) throws IOException {
        Scanner s = new Scanner(stream, "UTF-8").useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public static String extract(String pattern, String haystack) {
        Pattern p = Pattern.compile(".*?" + pattern + ".*?");
        Matcher m = p.matcher(haystack);
        if (!m.matches()) {
            throw new RuntimeException("Invalid text, regex not found: \"" + haystack + "\"");
        }

        return m.group(1);
    }

}
