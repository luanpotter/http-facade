package xyz.luan.facade;

import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class IntegrationTest {

	@Test
	@Ignore
	public void get() throws IOException {
		HttpFacade req = new HttpFacade("http://luan.xyz");
		Response resp = req.get();

		assertEquals(200, resp.status());
	}
}
