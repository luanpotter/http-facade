package xyz.luan.facade;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IntegrationTest {

	@Test
	@Disabled
	public void get() throws IOException {
		HttpFacade req = new HttpFacade("http://luan.xyz");
		Response resp = req.get();

		assertEquals(200, resp.status());
	}

	@Test
	@Disabled
	public void testDisableSSLValidation() throws IOException {
		HttpFacade req = new HttpFacade("https://viaja.dotz.com.br");
		req.disableSecuritySSLCertificateValidation();
		Response resp = req.get();

		assertEquals(301, resp.status());
	}
}
