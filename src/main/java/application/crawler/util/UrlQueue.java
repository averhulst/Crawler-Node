package application.crawler.util;

import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class UrlQueue {
    private String nextUrl;
    private String[] uninterestingFiletypes;
    private Queue<String> queue = new ArrayDeque<String>();
    private int queueSize;

    public UrlQueue(){
        uninterestingFiletypes = new String[]
                {
                    ".jpg",".jpeg",".png",".tiff",".gif",".gif", ".pdf", ".tiff",".doc",
                    ".js",".css"
                };
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
                //System.out.println("enqueuing: " + newUrl);
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
        for(String extension : uninterestingFiletypes){
            if(url.endsWith(extension) || url.endsWith(extension + "/")){
                return false;
            }
        }
        return true;
    }
}


