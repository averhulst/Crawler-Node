package application.crawler;

import java.net.MalformedURLException;
import java.util.*;

public class UrlQueue {
    private Queue<Url> queue = new ArrayDeque<Url>();
    private int queueSize;

    public UrlQueue(){
        queueSize = 0;
    }

    public synchronized Url getNext(){
        if(getSize() > 0){
            Url nextUrl = queue.poll();
            queueSize--;
            return nextUrl;
        }else{
            return null;
        }

    }
    public synchronized void enqueueUrl(Url newUrl){
        if(!queue.contains(newUrl)){
            queue.add(newUrl);
            queueSize++;
        }
    }

    public synchronized void enqueueUrl(String newUrl){
        Url url = null;
        try {
            url = new Url(newUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        enqueueUrl(url);
    }

    public boolean containsURL(Url url){
        return queue.contains(url);
    }

    public synchronized int getSize(){
        return queueSize;
    }

    public List<Url> toList(){
        List<Url> queueList = new ArrayList();
        for(Url url : queue){
            queueList.add(url);
        }
        return queueList;
    }

    public boolean hasNext(){
        return queue.size() > 0;
    }
}


