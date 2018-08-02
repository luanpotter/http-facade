package xyz.luan.facade.mock;

import xyz.luan.facade.HttpFacade;
import xyz.luan.facade.Response;

import java.io.IOException;
import java.net.MalformedURLException;

public class MockedHttpFacade extends HttpFacade {

    private Response mockResponse;

    public MockedHttpFacade(String baseUrl) throws MalformedURLException {
        super(baseUrl);
    }

    public MockedHttpFacade mockResponse(Response mock) {
        this.mockResponse = mock;
        return this;
    }

    @Override
    public Response req() throws IOException {
        return mockResponse;
    }
}
