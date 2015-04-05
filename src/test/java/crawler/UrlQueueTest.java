package crawler;

import application.crawler.util.UrlQueue;
import org.junit.Test;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class UrlQueueTest {

    @Test
    public void testGetNext() throws Exception {
        UrlQueue urlQueue = new UrlQueue();

        urlQueue.enqueueUrl("http://www.google.com");
        urlQueue.enqueueUrl("http://www.youtube.com");
        urlQueue.enqueueUrl("http://www.netflix.com");
        urlQueue.enqueueUrl("http://www.reddit.com");

        assert(urlQueue.getNext().equals("http://www.google.com"));

        assert(urlQueue.getNext().equals("http://www.youtube.com"));
        assert(urlQueue.getNext().equals("http://www.netflix.com"));
        assert(urlQueue.getNext().equals("http://www.reddit.com"));

        urlQueue = new UrlQueue();

        urlQueue.enqueueUrl(new URI("http://www.google.com"));
        urlQueue.enqueueUrl(new URI("http://www.youtube.com"));
        urlQueue.enqueueUrl(new URI("http://www.reddit.com"));

        assert(urlQueue.getNext().equals(new URL("http://www.google.com").toString()));
        assert(urlQueue.getNext().equals(new URL("http://www.youtube.com").toString()));
        assert(urlQueue.getNext().equals(new URL("http://www.reddit.com").toString()));

    }

    @Test
    public void testEnqueueUrl() throws Exception {
        UrlQueue urlQueue = new UrlQueue();
        String urlStr = "http://www.netflix.com";

        urlQueue.enqueueUrl("http://www.netflix.com");
        assert(urlQueue.getNext().equals(urlStr));

        urlQueue = new UrlQueue();
        URI url = new URI("http://www.netflix.com");

        urlQueue.enqueueUrl(url);
        assert(urlQueue.getNext().equals(url.toString()));
    }

    @Test
    public void testContainsURL() throws Exception {
        UrlQueue urlQueue = new UrlQueue();
        String urlStr = "http://www.netflix.com";

        urlQueue.enqueueUrl("http://www.netflix.com");
        assert(urlQueue.containsURL(urlStr));

        urlQueue = new UrlQueue();
        URI url = new URI("http://www.netflix.com");

        urlQueue.enqueueUrl(url);
        assert(urlQueue.containsURL(url.toString()));
    }


    @Test
    public void testGetSize() throws Exception {
        UrlQueue urlQueue = new UrlQueue();

        urlQueue.enqueueUrl("http://www.google.com");
        urlQueue.enqueueUrl("http://www.youtube.com");
        urlQueue.enqueueUrl("http://www.reddit.com");
        urlQueue.enqueueUrl("http://www.cnn.com");

        assert(urlQueue.getSize() == 4);
    }

    @Test
    public void testToList() throws Exception {
        UrlQueue urlQueue = new UrlQueue();

        ArrayList<String> urlQueueList = new ArrayList<String>(){{
            add("http://www.google.com");
            add("http://www.youtube.com");
            add("http://www.reddit.com");
            add("http://www.cnn.com");
        }};

        for(String s : urlQueueList){
            urlQueue.enqueueUrl(s);
        }

        List<String> resultList = urlQueue.toList();

        assertEquals(urlQueueList, resultList);
    }

    @Test
    public void testShouldCrawl() throws Exception {

    }
}
