package xyz.luan.facade;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class HttpFacade {

    private String method;
    private String baseUrl;
    private List<Entry<String, String>> headers, queries;
    private Object body;
    private boolean isGzip;
    private Integer timeout = 3 * 60 * 1000;
    private boolean followRedirects;

    public HttpFacade(String baseUrl) {
        this.baseUrl = baseUrl;
        this.headers = new ArrayList<>();
        this.queries = new ArrayList<>();
        this.followRedirects = false;
    }

    public HttpFacade timeout(int ms) {
        this.timeout = ms;
        return this;
    }

    public HttpFacade noTimeout() {
        this.timeout = null;
        return this;
    }

    public HttpFacade followRedirects() {
        this.followRedirects = true;
        return this;
    }

    public HttpFacade cookies(Map<String, String> cookies) {
        StringBuilder str = new StringBuilder();
        for (Entry<String, String> c : cookies.entrySet()) {
            str.append(c.getKey()).append("=").append(c.getValue()).append("; ");
        }
        header("Cookie", str.toString());
        return this;
    }

    public HttpFacade header(String k, String v) {
        this.headers.add(new SimpleEntry<>(k, v));
        return this;
    }

    public HttpFacade gzip(String acceptContent) {
        isGzip = true;
        return header("Accept-Encoding", acceptContent);
    }

    public HttpFacade query(String k, String v) {
        this.queries.add(new SimpleEntry<>(k, v));
        return this;
    }

    public HttpFacade body(Object body) {
        this.body = body;
        return this;
    }

    public HttpURLConnection generateConnection() throws IOException {
        URL obj = new URL(getUrl());
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setInstanceFollowRedirects(followRedirects);
        con.setRequestMethod(method);
        if (timeout != null) {
            con.setConnectTimeout(timeout);
        }
        setHeaders(con);
        setBody(con);
        return con;
    }

    public String getUrl() {
        String queryStr = urlEncodeUTF8(queries);
        return baseUrl + (queryStr.isEmpty() ? "" : "?" + queryStr);
    }

    public static String urlEncodeUTF8(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    private static String urlEncodeUTF8(List<Entry<String, String>> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<?, ?> entry : map) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            Object v = entry.getValue();
            if (v != null) {
                sb.append(String.format("%s=%s", urlEncodeUTF8(entry.getKey().toString()), urlEncodeUTF8(v.toString())));
            }
        }
        return sb.toString();
    }

    private void setHeaders(HttpURLConnection con) {
        for (Entry<String, String> k : headers) {
            con.setRequestProperty(k.getKey(), k.getValue());
        }
    }

    private void setBody(HttpURLConnection con) throws IOException {
        if (body != null) {
            con.addRequestProperty("Content-Length", body.toString().length() + "");

            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(body.toString());
            wr.flush();
            wr.close();

        }
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public Response get() throws IOException {
        return method("GET").req();
    }

    public Response post() throws IOException {
        return method("POST").req();
    }

    public Response put() throws IOException {
        return method("PUT").req();
    }

    public Response delete() throws IOException {
        return method("DELETE").req();
    }

    public HttpFacade method(String method) {
        this.method = method;
        return this;
    }

    public Response req() throws IOException {
        return new Response(generateConnection(), isGzip);
    }

}
