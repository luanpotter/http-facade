package xyz.luan.facade;

import java.net.MalformedURLException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static xyz.luan.facade.Util.urlDecodeUTF8;
import static xyz.luan.facade.Util.urlEncodeUTF8;

class UrlFacade {
	private static final String URL_SPLIT_REGEX = "^(([^:\\/?#]+):)?(\\/\\/([^\\/?#]*))?([^?#]*)(\\\\?([^#]*))?(#(.*))?";
	private static final int NO_PORT = -1;
	private static final String DEFAULT_PROTOCOL = "http";
	private String urlParsed;
	private String urlString;
	private String protocol = DEFAULT_PROTOCOL;
	private String auth;
	private String host;
	private int port = NO_PORT;
	private String path = "";
	private List<Entry<String, String>> queryParams;

	public UrlFacade(String urlString) throws MalformedURLException {
		this.urlString = urlString;
		parse();
	}

	private void parse() throws MalformedURLException {
		checkProtocol();
		Pattern p = Pattern.compile(URL_SPLIT_REGEX);
		Matcher matcher = p.matcher(this.urlString);
		if (matcher.find()) {
			this.path = matcher.group(5);
			String queryString = matcher.group(7);
			this.queryParams = parseQueryParams(queryString.startsWith("?") ? queryString.substring(1) : queryString);

			splitAuthHostPort(matcher.group(4));
		}
		this.urlParsed = getFullUrl();
	}

	public void splitAuthHostPort(String fullHost) throws MalformedURLException {
		String[] splited = fullHost.split("@");
		if (splited.length == 2) {
			this.auth = splited[0];
			int index = 1;
			setHostAndPort(splited, index);
		} else if (splited.length == 1) {
			int index = 0;
			setHostAndPort(splited, index);
		}
	}

	private void setHostAndPort(String[] splited, int index) throws MalformedURLException {
		String[] allHostAndPort = splited[index].split(":");
		this.host = allHostAndPort[0];

		if (allHostAndPort.length == 2) {
			try {
				this.port = Integer.parseInt(allHostAndPort[1]);
			} catch (NumberFormatException e) {
				throw new MalformedURLException("Invalid port number :" + allHostAndPort[1]);
			}
		}
	}

	private void checkProtocol() {
		String[] protocolSplited = this.urlString.split("://");
		if (protocolSplited.length < 2) {
			this.protocol = DEFAULT_PROTOCOL;
			this.urlString = this.protocol + "://" + this.urlString;
		} else {
			this.protocol = protocolSplited[0];
		}
	}

	private String getFullUrl() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.protocol).append("://");
		if (hasAuth())
			sb.append(this.auth).append("@");
		sb.append(this.host);
		if (hasPort())
			sb.append(":").append(this.port);

		sb.append(this.path);

		return sb.toString();
	}

	private boolean hasPort() {
		return this.port != NO_PORT;
	}

	private boolean hasAuth() {
		return this.auth != null && this.auth.trim().length() > 0;
	}

	private List<Entry<String,String>> parseQueryParams(String query) {
		List<Entry<String, String>> queries = new ArrayList<>();
		if (query.trim().isEmpty()) {
			return queries;
		}

		String[] paramsAndValues = query.split("&");
		for (String paramAndValue : paramsAndValues) {
			if (paramAndValue == null || paramAndValue.trim().length() == 0)
				continue;
			String[] split = paramAndValue.split("=");
			String key = urlDecodeUTF8(split[0]);
			if (split.length == 2) {
				queries.add(new SimpleEntry<>(key, urlDecodeUTF8(split[1])));
			} else {
				queries.add(new SimpleEntry<>(key, ""));
			}
		}
		return queries;
	}

	public List<Entry<String, String>> getQueryParams() {
		return queryParams;
	}

	public void setQueryParams(List<Entry<String, String>> queryParams) {
		this.queryParams = queryParams;
	}

	public int getPort() {
		return this.port;
	}

	public String getUrlString() {
		return urlString;
	}

	public void setUrlString(String urlString) {
		this.urlString = urlString;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setAuth(String auth) {
		this.auth = auth;
	}

	public String getAuth() {
		return this.auth;
	}

	public String buildUrl() {
		String queryStr = urlEncodeUTF8(getQueryParams());
		return getFullUrl() + (queryStr.isEmpty() ? "" : "?" + queryStr);
	}

	public String getUrlParsed() {
		return urlParsed;
	}

	public String user(String user, String pass) {
		String token = user + ":" + pass;
		this.auth = token;
		this.urlParsed = getFullUrl();
		return Util.encodeBase64(token);
	}

}