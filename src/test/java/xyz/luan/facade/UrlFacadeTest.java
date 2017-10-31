package xyz.luan.facade;

import java.net.MalformedURLException;

import org.junit.Assert;
import org.junit.Test;

public class UrlFacadeTest {

	@Test
	public void test() throws MalformedURLException {
		UrlFacade facade = new UrlFacade("https://dan:123@regex101.com:8080/blablabla/blebleble?bli=blo&blu=hgfhgf");
		Assert.assertEquals("https://dan:123@regex101.com:8080/blablabla/blebleble", facade.getFullHost());
	}

	@Test
	public void getAuth() throws MalformedURLException {
		UrlFacade facade = new UrlFacade("https://dan:123@regex101.com:8080/blablabla/blebleble?bli=blo&blu=hgfhgf");
		Assert.assertEquals("dan:123", facade.getAuth());

		facade = new UrlFacade("https://regex101.com:8080/blablabla/blebleble?bli=blo&blu=hgfhgf");
		Assert.assertNull(facade.getAuth());
	}

	@Test
	public void getQueries() throws MalformedURLException {
		UrlFacade facade = new UrlFacade("https://dan:123@regex101.com:8080/blablabla/blebleble?bli=blo&blu=hgfhgf");
		Assert.assertEquals(2, facade.getQueries().size());

		facade = new UrlFacade("https://dan:123@regex101.com:8080/blablabla/blebleble?bli=blo&blu=hgfhgf&xpto=");
		Assert.assertEquals(3, facade.getQueries().size());

		facade = new UrlFacade("https://dan:123@regex101.com:8080/blablabla/blebleble?bli=blo&blu=hgfhgf&xpto=&qwe=");
		Assert.assertEquals(4, facade.getQueries().size());
	}

	@Test
	public void testDefaultProtocol() throws MalformedURLException {
		UrlFacade facade = new UrlFacade("dan:123@regex101.com:8080/blablabla/blebleble?bli=blo&blu=hgfhgf");
		Assert.assertEquals("http://dan:123@regex101.com:8080/blablabla/blebleble", facade.getFullHost());

		facade = new UrlFacade("https://dan:123@regex101.com:8080/blablabla/blebleble?bli=blo&blu=hgfhgf");
		Assert.assertEquals("https://dan:123@regex101.com:8080/blablabla/blebleble", facade.getFullHost());

		facade = new UrlFacade("http://dan:123@regex101.com:8080/blablabla/blebleble?bli=blo&blu=hgfhgf");
		Assert.assertEquals("http://dan:123@regex101.com:8080/blablabla/blebleble", facade.getFullHost());
	}
}
