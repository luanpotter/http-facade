package xyz.luan.facade;

import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.util.AbstractMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class UrlFacadeTest {

	@Test
	public void test() throws MalformedURLException {
		UrlFacade facade = new UrlFacade("https://dan:123@regex101.com:8080/blablabla/blebleble?bli=blo&blu=hgfhgf");
		assertEquals("https://dan:123@regex101.com:8080/blablabla/blebleble", facade.getUrlParsed());
	}

	@Test
	public void getAuth() throws MalformedURLException {
		UrlFacade facade = new UrlFacade("https://dan:123@regex101.com:8080/blablabla/blebleble?bli=blo&blu=hgfhgf");
		assertEquals("dan:123", facade.getAuth());

		facade = new UrlFacade("https://regex101.com:8080/blablabla/blebleble?bli=blo&blu=hgfhgf");
		assertNull(facade.getAuth());
	}

	@Test
	public void getQueries() throws MalformedURLException {
		UrlFacade facade = new UrlFacade("https://dan:123@regex101.com:8080/blablabla/blebleble?bli=blo&blu=hgfhgf");
		assertEquals(2, facade.getQueryParams().size());

		facade = new UrlFacade("https://dan:123@regex101.com:8080/blablabla/blebleble?bli=blo&blu=hgfhgf&xpto=");
		assertEquals(3, facade.getQueryParams().size());

		facade = new UrlFacade("https://dan:123@regex101.com:8080/blablabla/blebleble?bli=blo&blu=hgfhgf&xpto=&qwe=");
		assertEquals(4, facade.getQueryParams().size());

		facade = new UrlFacade("https://dan:123@regex101.com:8080/blablabla/blebleble");
		assertEquals(0, facade.getQueryParams().size());
	}

	@Test
	public void addQueries() throws MalformedURLException {
		UrlFacade facade = new UrlFacade("https://test.com?foo=bar");
		facade.getQueryParams().add(new AbstractMap.SimpleEntry<>("a", "2"));

		assertEquals(2, facade.getQueryParams().size());
		assertEquals("bar", facade.getQueryParams().get(0).getValue());
		assertEquals("a", facade.getQueryParams().get(1).getKey());
	}

	@Test
	public void testDefaultProtocol() throws MalformedURLException {
		UrlFacade facade = new UrlFacade("dan:123@regex101.com:8080/blablabla/blebleble?bli=blo&blu=hgfhgf");
		assertEquals("http://dan:123@regex101.com:8080/blablabla/blebleble", facade.getUrlParsed());

		facade = new UrlFacade("https://dan:123@regex101.com:8080/blablabla/blebleble?bli=blo&blu=hgfhgf");
		assertEquals("https://dan:123@regex101.com:8080/blablabla/blebleble", facade.getUrlParsed());

		facade = new UrlFacade("http://dan:123@regex101.com:8080/blablabla/blebleble?bli=blo&blu=hgfhgf");
		assertEquals("http://dan:123@regex101.com:8080/blablabla/blebleble", facade.getUrlParsed());

		facade = new UrlFacade("http://dan:123@regex101.com/blablabla/blebleble?bli=blo&blu=hgfhgf");
		assertEquals("http://dan:123@regex101.com/blablabla/blebleble", facade.getUrlParsed());
	}

	@Test
	public void testParse() throws MalformedURLException {
		UrlFacade facade = new UrlFacade("http://dan:123@regex101.com/blablabla/blebleble?bli=blo&blu=hgfhgf");
		assertEquals(-1, facade.getPort());
		assertEquals("dan:123", facade.getAuth());
		assertEquals("regex101.com", facade.getHost());
		assertEquals("/blablabla/blebleble", facade.getPath());
		assertEquals("http", facade.getProtocol());
		assertEquals("http://dan:123@regex101.com/blablabla/blebleble?bli=blo&blu=hgfhgf", facade.buildUrl());
	}

	@Test
	public void testProtocol() throws MalformedURLException {
		UrlFacade facade = new UrlFacade("http://dan:123@regex101.com/blablabla/blebleble?bli=blo&blu=hgfhgf");
		assertEquals("http", facade.getProtocol());

		facade = new UrlFacade("https://dan:123@regex101.com/blablabla/blebleble?bli=blo&blu=hgfhgf");
		assertEquals("https", facade.getProtocol());

		facade = new UrlFacade("dan:123@regex101.com/blablabla/blebleble?bli=blo&blu=hgfhgf");
		assertEquals("http", facade.getProtocol());
		assertEquals("http://dan:123@regex101.com/blablabla/blebleble?bli=blo&blu=hgfhgf", facade.getUrlString());

		facade = new UrlFacade("ftp://dan:123@regex101.com/blablabla/blebleble?bli=blo&blu=hgfhgf");
		assertEquals("ftp", facade.getProtocol());

		facade = new UrlFacade("xablau://dan:123@regex101.com/blablabla/blebleble?bli=blo&blu=hgfhgf");
		assertEquals("xablau", facade.getProtocol());
	}

	@Test
	public void testUser() throws MalformedURLException {
		UrlFacade facade = new UrlFacade("http://dan:123@regex101.com/blablabla/blebleble?bli=blo&blu=hgfhgf");
		assertEquals("dan:123", facade.getAuth());
		assertEquals("http://dan:123@regex101.com/blablabla/blebleble?bli=blo&blu=hgfhgf", facade.buildUrl());
		assertEquals("http://dan:123@regex101.com/blablabla/blebleble", facade.getUrlParsed());

		facade.user("dandan", "321");
		assertEquals("dandan:321", facade.getAuth());
		assertEquals("http://dandan:321@regex101.com/blablabla/blebleble?bli=blo&blu=hgfhgf", facade.buildUrl());
		assertEquals("http://dandan:321@regex101.com/blablabla/blebleble", facade.getUrlParsed());
	}

}
