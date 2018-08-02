package xyz.luan.facade.mock;

import xyz.luan.facade.Response;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MockedResponse extends Response {

    private int mockedStatus;
    private String mockedError;
    private Map<String, String> mockedCookies;
    private Map<String, List<String>> mockedHeaders;

    public MockedResponse(HttpURLConnection conn, boolean isGzip, boolean storeContent) {
        super(conn, isGzip, storeContent);
    }

    public MockedResponse() {
        super(null, false, false);
        this.mockedHeaders = new HashMap<>();
        this.mockedCookies = new HashMap<>();
    }

    public static MockedResponse build() {
        return new MockedResponse();
    }

    public MockedResponse withContent(String content) {
        this.content = content;
        return this;
    }

    @Override
    public String content() throws IOException {
        return this.content;
    }

    public MockedResponse withStatus(int status) {
        this.mockedStatus = status;
        return this;
    }

    @Override
    public int status() throws IOException {
        return this.mockedStatus;
    }

    public MockedResponse withError(int status, String error) {
        this.mockedStatus = status;
        this.mockedError = error;
        return this;
    }

    @Override
    public String error() throws IOException {
        return this.mockedError;
    }

    public MockedResponse withCookies(Map<String, String> cookies) {
        this.mockedCookies = cookies;
        return this;
    }

    @Override
    public Map<String, String> cookies() {
        return this.mockedCookies;
    }

    public MockedResponse withHeaders(Map<String, List<String>> headers) {
        this.mockedHeaders = headers;
        return this;
    }

    public MockedResponse withHeader(String key, String value) {
        this.mockedHeaders.put(key, Arrays.asList(value));
        return this;
    }

    public MockedResponse withHeaders(String key, List<String> values) {
        this.mockedHeaders.put(key, values);
        return this;
    }

    @Override
    public String header(String key) {
        return this.mockedHeaders.get(key).get(0);
    }

    @Override
    public List<String> headers(String key) {
        return this.mockedHeaders.get(key);
    }

    @Override
    public Map<String, List<String>> headers() {
        return this.mockedHeaders;
    }

    @Override
    public String location() {
        return header("Location");
    }
}

