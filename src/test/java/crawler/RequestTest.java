package crawler;

import application.crawler.Request;
import application.crawler.Url;
import org.junit.Before;
import org.junit.Test;

public class RequestTest {
    private Request request;

    @Before
    public void setUp() throws Exception {
        request = new Request(new Url("http://www.google.com/"));
        request.setConnectionTimeout(5000);
        request.setRequestMethod("GET");
        request.setUserAgent("Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
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
