package application.crawler.domain;

import application.crawler.util.Request;
import org.junit.Test;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: buttes
 * Date: 3/30/15
 * Time: 12:37 AM
 * To change this template use File | Settings | File Templates.
 */
public class PageTest {
    private Page page;
    @Test
    public void testGetUrl() throws Exception {
        Request request = new Request(new URL("http://jgrapht.org/"));

        page = new Page(new URL("http://jgrapht.org/"), request.getResponse());

        List<String> discoveredDomains = new ArrayList<>();


    }

    @Test
    public void testGetDiscoveredDomains() throws Exception {

    }

    @Test
    public void testGetDiscoveredPages() throws Exception {

    }
}
