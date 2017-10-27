package xyz.luan.facade;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HttpFacadeTest {

	@Test
	public void userTestNoProtocol() {
		HttpFacade facade = new HttpFacade("luan.xyz");
		facade.user("luan", "mypass123");
		assertEquals("luan:mypass123@luan.xyz", facade.getUrl());
		assertEquals("Basic bHVhbjpteXBhc3MxMjM=", facade.header("Authentication"));
	}

	@Test
	public void userTestWithProtocol() {
		HttpFacade facade = new HttpFacade("https://luan.xyz");
		facade.user("luan", "mypass123");
		assertEquals("https://luan:mypass123@luan.xyz", facade.getUrl());
		assertEquals("Basic bHVhbjpteXBhc3MxMjM=", facade.header("Authentication"));
	}
}
