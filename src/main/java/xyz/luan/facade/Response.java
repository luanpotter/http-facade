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
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int numBytesRead;
        byte[] tempBytes = new byte[6000];
        while ((numBytesRead = gs.read(tempBytes, 0, tempBytes.length)) != -1) {
            baos.write(tempBytes, 0, numBytesRead);
        }
        return baos.toString();
    }

    public String error() throws IOException {
        return Util.toString(conn.getErrorStream());
    }

    public int status() throws IOException {
        return conn.getResponseCode();
    }

    public String location() {
        return conn.getHeaderField("Location");
    }

    public Map<String, String> cookies() {
        List<String> strings = conn.getHeaderFields().get("Set-Cookie");
        Map<String, String> cookies = new HashMap<>();
        for (String cookie : strings) {
            String name = Util.extract("^([a-zAA-Z0-9_]*)=", cookie);
            String value = Util.extract("^[a-zAA-Z0-9_]*=([^;]*)", cookie);
            cookies.put(name, value);
        }
        return cookies;
    }
}
