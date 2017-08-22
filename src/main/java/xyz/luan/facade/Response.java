package xyz.luan.facade;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class Response {

    private final HttpURLConnection conn;
    private final boolean isGzip;

    Response(HttpURLConnection conn, boolean isGzip) {
        this.conn = conn;
        this.isGzip = isGzip;
    }

    public String content() throws IOException {
        if (isGzip) {
            return contentGzip();
        }
        return Util.toString(conn.getInputStream());
    }

    private String contentGzip() throws IOException {
        GZIPInputStream gs = new GZIPInputStream(conn.getInputStream());
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        int numBytesRead;
        byte[] tempBytes = new byte[6000];
        while ((numBytesRead = gs.read(tempBytes, 0, tempBytes.length)) != -1) {
            stream.write(tempBytes, 0, numBytesRead);
        }
        return stream.toString();
    }

    public String error() throws IOException {
        return Util.toString(conn.getErrorStream());
    }

    public int status() throws IOException {
        return conn.getResponseCode();
    }

    public HttpURLConnection getConnection() {
        return conn;
    }

    public Map<String,List<String>> headers(){
        return conn.getHeaderFields();
    }

    public String header(String key) {
        return conn.getHeaderField(key);
    }

    public List<String> headers(String key) {
        return conn.getHeaderFields().get(key);
    }

    public String location() {
        return conn.getHeaderField("Location");
    }

    public Map<String, String> cookies() {
        List<String> strings = conn.getHeaderFields().get("Set-Cookie");
        Map<String, String> cookies = new HashMap<>();
        if (strings == null) {
            extractCookie(cookies, conn.getHeaderField("Set-Cookie"));
        } else {
            for (String cookie : strings) {
                extractCookie(cookies, cookie);
            }
        }
        return cookies;
    }

    private void extractCookie(Map<String, String> cookies, String cookie) {
        String name = Util.extract("^([a-zAA-Z0-9_]*)=", cookie);
        String value = Util.extract("^[a-zAA-Z0-9_]*=([^;]*)", cookie);
        cookies.put(name, value);
    }
    
    public static boolean isInformational(int code) {
        int status = status();
        return ((status <= code) && (code <= status));
    }

    public static boolean isSuccess(int code) {
        int status = status();
        return ((status <= code) && (code <= status));
    }

    public static boolean isRedirection(int code) {
        int status = status();
        return ((status <= code) && (code <= status));
    }

    public static boolean isClientError(int code) {
        int status = status();
        return ((status <= code) && (code <= status));
    }

    public static boolean isServerError(int code) {
        int status = status();
        return ((status <= code) && (code <= status));
    }
}
