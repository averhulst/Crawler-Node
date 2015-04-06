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

        assert(urlQueue.getNext().equals(new URI("http://www.google.com")));

        assert(urlQueue.getNext().equals(new URI("http://www.youtube.com")));
        assert(urlQueue.getNext().equals(new URI("http://www.netflix.com")));
        assert(urlQueue.getNext().equals(new URI("http://www.reddit.com")));

        urlQueue = new UrlQueue();

        urlQueue.enqueueUrl(new URI("http://www.google.com"));
        urlQueue.enqueueUrl(new URI("http://www.youtube.com"));
        urlQueue.enqueueUrl(new URI("http://www.reddit.com"));

        assert(urlQueue.getNext().equals(new URI("http://www.google.com")));
        assert(urlQueue.getNext().equals(new URI("http://www.youtube.com")));
        assert(urlQueue.getNext().equals(new URI("http://www.reddit.com")));

    }

    @Test
    public void testEnqueueUrl() throws Exception {
        UrlQueue urlQueue = new UrlQueue();
        URI url = new URI("http://www.netflix.com");
        urlQueue.enqueueUrl("http://www.netflix.com");
        urlQueue.enqueueUrl("http://www.google.com");
        urlQueue.enqueueUrl("http://www.reddit.com");

        assert(urlQueue.getNext().equals(url));
    }

    @Test
    public void testContainsURL() throws Exception {
        UrlQueue urlQueue = new UrlQueue();
        String urlStr = "http://www.netflix.com";

        urlQueue.enqueueUrl("http://www.netflix.com");
        assert(urlQueue.containsURL(new URI(urlStr)));

        urlQueue = new UrlQueue();
        URI url = new URI("http://www.netflix.com");

        urlQueue.enqueueUrl(url);
        assert(urlQueue.containsURL(url));
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

        List<URI> urlQueueList = new ArrayList<URI>(){{
            add(new URI("http://www.google.com"));
            add(new URI("http://www.youtube.com"));
            add(new URI("http://www.reddit.com"));
            add(new URI("http://www.cnn.com"));
        }};

        for(URI url : urlQueueList){
            urlQueue.enqueueUrl(url);
        }

        List<URI> resultList = urlQueue.toList();

        assertEquals(urlQueueList, resultList);
    }

    @Test
    public void testShouldCrawl() throws Exception {

    }
}
