package application.crawler.domain;

import application.crawler.util.Request;
import org.junit.Test;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;


public class PageTest {
    private Page page;
    @Test
    public void testGetUrl() throws Exception {
        Request request = new Request(new URI("http://jgrapht.org/"));

        page = new Page(new URI("http://jgrapht.org/"), request.getResponse());

        List<String> discoveredDomains = new ArrayList<>();


    }

    @Test
    public void testGetDiscoveredDomains() throws Exception {

    }

    @Test
    public void testGetDiscoveredPages() throws Exception {

    }
}
