package xyz.luan.facade;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

class UrlFacade {
	private static final String DEFAULT_PROTOCOL = "http";
	private URL url;
	private String urlString;

	public UrlFacade(String urlString) throws MalformedURLException {
		this.urlString = urlString;
		checkProtocol();
		this.url = new URL(this.urlString);
	}

	private void checkProtocol() {
		String protocol = this.urlString.substring(0, 8);
		boolean hasProtocol = protocol.contains("http://") || protocol.contains("https://");
		if (!hasProtocol)
			this.urlString = DEFAULT_PROTOCOL + "://" + this.urlString;
	}

	public String getFullHost() {
		StringBuilder sb = new StringBuilder();
		sb.append(url.getProtocol()).append("://");
		sb.append(url.getAuthority());
		sb.append(url.getPath());
		return sb.toString();
	}

	public String getAuth() {
		if (url.getAuthority().contains("@")) {
			return url.getAuthority().split("@")[0];
		}
		return null;
	}

	public List<Entry<String, String>> getQueries() {
		List<Entry<String, String>> queries = new ArrayList<>();
		String queryString = url.getQuery();
		String[] paramsAndValues = queryString.split("&");
		for (String paramAndValue : paramsAndValues) {
			String[] splited = paramAndValue.split("=");
			if (splited.length == 2) {
				queries.add(new SimpleEntry<>(splited[0], splited[1]));
			} else {
				queries.add(new SimpleEntry<>(splited[0], ""));
			}
		}
		return queries;
	}
}