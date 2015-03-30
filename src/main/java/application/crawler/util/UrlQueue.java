package application.crawler.util;

import java.net.URL;
import java.util.*;

public class UrlQueue {
    private String nextUrl;
    private Queue<String> queue = new ArrayDeque<String>();
    private int queueSize;

    private final List<String> FILE_TYPE_BLACKLIST =
        new ArrayList<>(
            Arrays.asList(
                new String[]{
                        ".jpg", ".jpeg", ".png", ".tiff",
                        ".gif", ".rif",  ".bmp", ".pdf",
                        ".doc", ".js",   ".css"
                }
            )
        );

    public UrlQueue(){
        queueSize = 0;
    }
    public synchronized String getNext(){
        if(getSize() > 0){
            nextUrl = queue.poll();
            queueSize--;
            return nextUrl;
        }else{
            return null;
        }

    }
    public synchronized void enqueueUrl(String newUrl){
        if(newUrl.length() > 4){

            if(!queue.contains(newUrl) && shouldCrawl(newUrl)){
                queue.add(newUrl);
                queueSize++;
            }
        }
    }

    public synchronized void enqueueUrl(URL newUrl){
        enqueueUrl(newUrl.toString());
    }
    public synchronized boolean containsURL(String s){
        return queue.contains(s);
    }
    public boolean containsURL(URL url){
        if(queue.contains(url.getHost())){
            System.exit(9000);
        }
        return queue.contains(url.getHost());
    }
    public synchronized int getSize(){
        return queueSize;
    }
    public List<String> toList(){
        ArrayList queueList = new ArrayList();
        for(String s : queue){
            queueList.add(s);
        }
        return queueList;
    }
    public boolean shouldCrawl(String url){
        for(String extension : FILE_TYPE_BLACKLIST){
            if(url.endsWith(extension) || url.endsWith(extension + "/")){
                return false;
            }
        }
        return true;
    }
    public boolean hasNext(){
        return queue.size() > 0;
    }
}


