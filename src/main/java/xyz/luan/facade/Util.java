package xyz.luan.facade;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Map;
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
        return extractAll(pattern, haystack).group(1);
    }

    public static Matcher extractAll(String pattern, String haystack) {
        Pattern p = Pattern.compile(".*?" + pattern + ".*?");
        Matcher m = p.matcher(haystack);
        if (!m.matches()) {
            throw new RuntimeException("Invalid text, regex not found: \"" + haystack + "\"");
        }
        return m;
    }

    public static String encodeBase64(String str) {
        return Base64.encode(str.getBytes());
    }

    public static String urlDecodeUTF8(String s) {
        try {
            return URLDecoder.decode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public static String urlEncodeUTF8(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public static String urlEncodeUTF8(Collection<Map.Entry<String, String>> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : map) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            Object v = entry.getValue();
            if (v != null) {
                String key = urlEncodeUTF8(entry.getKey());
                String value = urlEncodeUTF8(v.toString());
                if (value.isEmpty()) {
                    sb.append(String.format("%s", key));
                } else {
                    sb.append(String.format("%s=%s", key, value));
                }
            }
        }
        return sb.toString();
    }
}
