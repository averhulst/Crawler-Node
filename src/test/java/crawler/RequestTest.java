package crawler;

import crawler.Request;
import org.junit.Before;
import org.junit.Test;

import java.net.URL;

public class RequestTest {
    private Request request;

    @Before
    public void setUp() throws Exception {
        request = new Request(new URL("http://www.google.com/"));
        request.setConnectionTimeout(5000);
        request.setRequestMethod("GET");
        request.setUserAgent("Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");


    }

    @Test
    public void testConnect() throws Exception {
        request.connect();
    }

    @Test
    public void testGetResponseCode() throws Exception {
        assert request.getResponseCode() == 200;
    }

    @Test
    public void testGetResponse() throws Exception {
        assert request.getResponse().contains("Search the world's information");
    }

}
