package application.crawler;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class UrlQueue {
    private Queue<URI> queue = new ArrayDeque<URI>();
    private int queueSize;

    public UrlQueue(){
        queueSize = 0;
    }

    public synchronized URI getNext(){
        if(getSize() > 0){
            URI nextURI = queue.poll();
            queueSize--;
            return nextURI;
        }else{
            return null;
        }

    }
    public synchronized void enqueueURI(URI newURI){
        if(!queue.contains(newURI)){
            queue.add(newURI);
            queueSize++;
        }
    }

    public synchronized void enqueueURI(String newURI){
        URI url = null;
        try {
            url = new URI(newURI);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        enqueueURI(url);
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


