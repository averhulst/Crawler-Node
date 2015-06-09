package application.crawler;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class UrlQueue {
    private Queue<URI> queue = new ArrayDeque<URI>();
    private int queueSize;
    private URLFilter URLFilter = new URLFilter();

    public UrlQueue(){
        queueSize = 0;
    }

    public synchronized URI getNext(){
        if(getSize() > 0){
            URI nextUrl = queue.poll();
            queueSize--;
            return nextUrl;
        }else{
            return null;
        }

    }
    public synchronized void enqueueUrl(URI newUrl){
        if(!queue.contains(newUrl) && URLFilter.isCrawlable(newUrl)){
            queue.add(newUrl);
            queueSize++;
        }
    }

    public synchronized void enqueueUrl(String newUrl){
        URI url = null;
        try {
            url = new URI(newUrl);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        enqueueUrl(url);
    }

    public boolean containsURL(URI url){
        return queue.contains(url);
    }

    public synchronized int getSize(){
        return queueSize;
    }

    public List<URI> toList(){
        List<URI> queueList = new ArrayList();
        for(URI url : queue){
            queueList.add(url);
        }
        return queueList;
    }

    public boolean hasNext(){
        return queue.size() > 0;
    }
}


