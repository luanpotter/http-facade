package xyz.luan.facade;

import static org.junit.Assert.assertEquals;

import java.net.MalformedURLException;

import org.junit.Test;

public class HttpFacadeTest {

	@Test
	public void userTestNoProtocol() throws MalformedURLException {
		HttpFacade facade = new HttpFacade("luan.xyz");
		facade.user("luan", "mypass123");
		assertEquals("http://luan:mypass123@luan.xyz", facade.getUrl());
		assertEquals("Basic bHVhbjpteXBhc3MxMjM=", facade.header("Authentication"));
	}

	@Test
	public void userTestWithProtocol() throws MalformedURLException {
		HttpFacade facade = new HttpFacade("https://luan.xyz");
		facade.user("luan", "mypass123");
		assertEquals("https://luan:mypass123@luan.xyz", facade.getUrl());
		assertEquals("Basic bHVhbjpteXBhc3MxMjM=", facade.header("Authentication"));
	}
}
