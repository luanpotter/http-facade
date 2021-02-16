package xyz.luan.facade;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * TODO this test requires internet to work.
 * I don't think let's encrypt will ever go down, but we should add more
 * exhaustive integration tests with a local server.
 */
public class HttpFacadeSSLTest {

    @Test
    public void testAddCustomCertificate() throws IOException {
        Response r = new HttpFacade("https://helloworld.letsencrypt.org/")
                .addCustomCertificate(HttpFacadeTest.class.getResourceAsStream("/lets-encrypt.cer"))
                .get();
        assertEquals(200, r.status());
    }

    @Test
    @Deprecated
    public void testDisableSecurity() throws IOException {
        Response r = new HttpFacade("https://helloworld.letsencrypt.org/")
                .disableSecuritySSLCertificateValidation()
                .get();
        assertEquals(200, r.status());
    }
}
