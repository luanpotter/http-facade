package xyz.luan.facade;

import org.junit.Assert;
import org.junit.Test;

public class UrlFacadeTest {

	@Test
	public void test() {
		UrlFacade facade = new UrlFacade("https://dan:123@regex101.com:8080/blablabla/blebleble?bli=blo&blu=hgfhgf");
		Assert.assertEquals("https://dan:123@regex101.com:8080/blablabla/blebleble", facade.getUrlParsed());
	}

	@Test
	public void getAuth() {
		UrlFacade facade = new UrlFacade("https://dan:123@regex101.com:8080/blablabla/blebleble?bli=blo&blu=hgfhgf");
		Assert.assertEquals("dan:123", facade.getAuth());

		facade = new UrlFacade("https://regex101.com:8080/blablabla/blebleble?bli=blo&blu=hgfhgf");
		Assert.assertNull(facade.getAuth());
	}

	@Test
	public void getQueries() {

		UrlFacade facade = new UrlFacade("https://dan:123@regex101.com:8080/blablabla/blebleble?bli=blo&blu=hgfhgf");
		Assert.assertEquals(2, facade.getQueries().size());

		facade = new UrlFacade("https://dan:123@regex101.com:8080/blablabla/blebleble?bli=blo&blu=hgfhgf&xpto=");
		Assert.assertEquals(3, facade.getQueries().size());

		facade = new UrlFacade("https://dan:123@regex101.com:8080/blablabla/blebleble?bli=blo&blu=hgfhgf&xpto=&qwe=");
		Assert.assertEquals(4, facade.getQueries().size());

		facade = new UrlFacade("https://dan:123@regex101.com:8080/blablabla/blebleble");
		Assert.assertEquals(0, facade.getQueries().size());
	}

	@Test
	public void testDefaultProtocol() {
		UrlFacade facade = new UrlFacade("dan:123@regex101.com:8080/blablabla/blebleble?bli=blo&blu=hgfhgf");
		Assert.assertEquals("http://dan:123@regex101.com:8080/blablabla/blebleble", facade.getUrlParsed());

		facade = new UrlFacade("https://dan:123@regex101.com:8080/blablabla/blebleble?bli=blo&blu=hgfhgf");
		Assert.assertEquals("https://dan:123@regex101.com:8080/blablabla/blebleble", facade.getUrlParsed());

		facade = new UrlFacade("http://dan:123@regex101.com:8080/blablabla/blebleble?bli=blo&blu=hgfhgf");
		Assert.assertEquals("http://dan:123@regex101.com:8080/blablabla/blebleble", facade.getUrlParsed());

		facade = new UrlFacade("http://dan:123@regex101.com/blablabla/blebleble?bli=blo&blu=hgfhgf");
		Assert.assertEquals("http://dan:123@regex101.com/blablabla/blebleble", facade.getUrlParsed());
		Assert.assertEquals(80, facade.getPort());

	}

	@Test
	public void testParse() {
		UrlFacade facade = new UrlFacade("http://dan:123@regex101.com/blablabla/blebleble?bli=blo&blu=hgfhgf");
		Assert.assertEquals(80, facade.getPort());
		Assert.assertEquals("dan:123", facade.getAuth());
		Assert.assertEquals("regex101.com", facade.getHost());
		Assert.assertEquals("/blablabla/blebleble", facade.getPath());
		Assert.assertEquals("http", facade.getProtocol());
	}

	@Test
	public void testProtocol() {
		UrlFacade facade = new UrlFacade("http://dan:123@regex101.com/blablabla/blebleble?bli=blo&blu=hgfhgf");
		Assert.assertEquals("http", facade.getProtocol());

		facade = new UrlFacade("https://dan:123@regex101.com/blablabla/blebleble?bli=blo&blu=hgfhgf");
		Assert.assertEquals("https", facade.getProtocol());

		facade = new UrlFacade("dan:123@regex101.com/blablabla/blebleble?bli=blo&blu=hgfhgf");
		Assert.assertEquals("http", facade.getProtocol());
		Assert.assertEquals("http://dan:123@regex101.com/blablabla/blebleble?bli=blo&blu=hgfhgf", facade.getUrlString());

		facade = new UrlFacade("ftp://dan:123@regex101.com/blablabla/blebleble?bli=blo&blu=hgfhgf");
		Assert.assertEquals("ftp", facade.getProtocol());

		facade = new UrlFacade("xablau://dan:123@regex101.com/blablabla/blebleble?bli=blo&blu=hgfhgf");
		Assert.assertEquals("xablau", facade.getProtocol());
	}

	@Test
	public void testUser() {
		UrlFacade facade = new UrlFacade("http://dan:123@regex101.com/blablabla/blebleble?bli=blo&blu=hgfhgf");
		Assert.assertEquals("dan:123", facade.getAuth());
		Assert.assertEquals("http://dan:123@regex101.com/blablabla/blebleble?bli=blo&blu=hgfhgf", facade.buildUrl());
		Assert.assertEquals("http://dan:123@regex101.com/blablabla/blebleble", facade.getUrlParsed());

		facade.user("dandan", "321");
		Assert.assertEquals("dandan:321", facade.getAuth());
		Assert.assertEquals("http://dandan:321@regex101.com/blablabla/blebleble?bli=blo&blu=hgfhgf", facade.buildUrl());
		Assert.assertEquals("http://dandan:321@regex101.com/blablabla/blebleble", facade.getUrlParsed());
	}

}
