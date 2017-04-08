package xyz.luan.facade;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Util {

    private Util() {
        throw new RuntimeException("Should never be instantiated");
    }

    public static String toString(InputStream stream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
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
