package xyz.luan.facade;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

public class IntegrationTest {

    @Test
    @Ignore
    public void get() throws IOException {
        HttpFacade req = new HttpFacade("http://luan.xyz");
        Response resp = req.get();

        assertEquals(200, resp.status());
    }
}
