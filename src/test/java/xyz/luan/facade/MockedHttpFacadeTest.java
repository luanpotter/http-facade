package xyz.luan.facade;

import org.junit.Assert;
import org.junit.Test;
import xyz.luan.facade.mock.MockedHttpFacade;
import xyz.luan.facade.mock.MockedResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class MockedHttpFacadeTest {

    @Test
    public void testContent() throws IOException {
        String content = "{ \"xpto\" : 123}";
        MockedResponse mock = MockedResponse.build().withContent(content);
        MockedHttpFacade http = new MockedHttpFacade("").mockResponse(mock);

        Response post = http.post();
        Assert.assertEquals(content, post.content());

        Response get = http.get();
        Assert.assertEquals(content, get.content());

        Response put = http.put();
        Assert.assertEquals(content, put.content());

        Response delete = http.delete();
        Assert.assertEquals(content, put.content());
    }

    @Test
    public void testStatus() throws IOException {
        MockedResponse mock = MockedResponse.build().withStatus(200);
        MockedHttpFacade http = new MockedHttpFacade("").mockResponse(mock);

        Response post = http.post();
        Assert.assertEquals(200, post.status());

        Response get = http.get();
        Assert.assertEquals(200, get.status());

        Response put = http.put();
        Assert.assertEquals(200, put.status());

        Response delete = http.delete();
        Assert.assertEquals(200, delete.status());
    }

    @Test
    public void testError() throws IOException {
        int status = 403;
        String error = "Get out of my life!";
        MockedResponse mock = MockedResponse.build().withError(status, error);
        MockedHttpFacade http = new MockedHttpFacade("").mockResponse(mock);

        Response post = http.post();
        Assert.assertEquals(status, post.status());
        Assert.assertEquals(error, post.error());

        Response get = http.get();
        Assert.assertEquals(status, get.status());
        Assert.assertEquals(error, get.error());

        Response put = http.put();
        Assert.assertEquals(status, put.status());
        Assert.assertEquals(error, put.error());

        Response delete = http.delete();
        Assert.assertEquals(status, delete.status());
        Assert.assertEquals(error, delete.error());
    }

    @Test
    public void testHeader() throws IOException {
        List<String> salsicha = Arrays.asList("v1", "v2");
        MockedResponse mock = MockedResponse.build().withHeader("Location", "xablau").withHeaders("SALSICHA", salsicha);
        MockedHttpFacade http = new MockedHttpFacade("").mockResponse(mock);

        Response post = http.post();
        Assert.assertEquals("xablau", post.headers().get("Location").get(0));
        Assert.assertEquals(salsicha, post.headers().get("SALSICHA"));

        Response get = http.get();
        Assert.assertEquals("xablau", get.headers().get("Location").get(0));
        Assert.assertEquals(salsicha, get.headers().get("SALSICHA"));

        Response put = http.put();
        Assert.assertEquals("xablau", put.headers().get("Location").get(0));
        Assert.assertEquals(salsicha, put.headers().get("SALSICHA"));

        Response delete = http.delete();
        Assert.assertEquals("xablau", delete.headers().get("Location").get(0));
        Assert.assertEquals(salsicha, delete.headers().get("SALSICHA"));
    }
}
