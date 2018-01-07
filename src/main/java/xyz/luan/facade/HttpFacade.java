package xyz.luan.facade;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class HttpFacade {

	private String method;
	private List<Entry<String, String>> headers, formParams;
	private Object body;
	private boolean isGzip;
	private Integer timeout = 3 * 60 * 1000;
	private boolean followRedirects = false;
	private boolean fixedSize = false;
	private boolean storeContent = true;
	private UrlFacade url;

	public HttpFacade(String baseUrl) throws MalformedURLException {
		this.url = new UrlFacade(baseUrl);
		this.headers = new ArrayList<>();
		this.formParams = new ArrayList<>();
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
		this.url.getQueries().add(new SimpleEntry<>(k, v));
		return this;
	}

	public HttpFacade body(Object body) {
		this.body = body;
		return this;
	}

	public HttpFacade withFixedSize() {
		this.fixedSize = true;
		return this;
	}

	public HttpFacade user(String user, String pass) {
		String token = url.user(user, pass);
		header("Authentication", "Basic " + token);
		return this;
	}

	public HttpFacade noStoredContent() {
		this.storeContent = false;
		return this;
	}

	public HttpFacade storedContent() {
		this.storeContent = true;
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
		if (fixedSize) {
			con.setFixedLengthStreamingMode(body.toString().length());
		}
		setHeaders(con);
		setBody(con);
		return con;
	}

	public String getUrl() {
		return url.buildUrl();
	}

	public static String urlEncodeUTF8(String s) {
		try {
			return URLEncoder.encode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new UnsupportedOperationException(e);
		}
	}

	public static String urlEncodeUTF8(Collection<Entry<String, String>> map) {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : map) {
			if (sb.length() > 0) {
				sb.append("&");
			}
			Object v = entry.getValue();
			if (v != null) {
				sb.append(String.format("%s=%s", urlEncodeUTF8(entry.getKey()), urlEncodeUTF8(v.toString())));
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
		if (!formParams.isEmpty() && body != null) {
			throw new RuntimeException("You can only specify body or form params, not both!");
		}
		String str = generateBody();
		if (str != null) {
			con.setDoOutput(true);
			con.addRequestProperty("Content-Length", str.length() + "");
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(str);
			wr.flush();
			wr.close();

		}
	}

	private String generateBody() {
		if (body != null) {
			return body.toString();
		}
		if (!formParams.isEmpty()) {
			return urlEncodeUTF8(formParams);
		}
		return null;
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
		return new Response(generateConnection(), isGzip, this.storeContent);
	}

	public String header(String name) {
		for (Entry<String, String> header : this.headers) {
			if (header.getKey().equals(name)) {
				return header.getValue();
			}
		}
		return null;
	}

	public HttpFacade formParam(String key, String val) {
		formParams.add(new SimpleEntry<>(key, val));
		header("Content-Type", "application/x-www-form-urlencoded");
		return this;
	}
}
