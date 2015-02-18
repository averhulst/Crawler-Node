package crawler;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Domain implements Runnable{
    private List<String> failedUrls;
    private List<String> disallowedPaths;
    private List<String> crawledUrls;
    private int crawlDelay;
    private int successfulPageCrawls;
    private long crawlStartTime;
    private long crawlElapsedTime;
    private boolean running;
    private URL domainURL;
    private Robotstxt robotsTxt;
    private UrlQueue pageQueue;
    private Timer timer = new Timer();
    private UrlQueue discoveredDomains;

    public Domain(URL url) {
        this.domainURL = url;
        failedUrls = new ArrayList<>();
        crawledUrls = new ArrayList<>();
        discoveredDomains = new UrlQueue();
        crawlStartTime = System.currentTimeMillis();
        pageQueue = new UrlQueue();
        pageQueue.enqueueUrl(url);
        readRobotsTxt(url);
    }

    private void readRobotsTxt(URL url){
        robotsTxt = new Robotstxt(url);
        robotsTxt.fetch();
        disallowedPaths = robotsTxt.getDisallowedPaths();
        crawlDelay = robotsTxt.getCrawlDelay();
    }

    public void run(){
        running = true;
        while(pageQueue.getSize() > 0){
            URL pageUrl;
            try {
                String next = pageQueue.getNext();
                //System.out.println("crawling: " + next);
                pageUrl = new URL(next);
                crawlPage(pageUrl);
                successfulPageCrawls ++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        crawlElapsedTime = System.currentTimeMillis() - crawlStartTime;
        //logDomainCrawl();

    }

    private void crawlPage(URL url){
        timer.start();
        Request request = new Request(url);
        if(request.getResponseCode() == 200){
            Page p = new Page(url.toString(), request.getResponse());
            p.parsePage();
            p.insertPage();
            successfulPageCrawls ++;
            processUrls(p.getCrawlableUrls());
            crawledUrls.add(url.toString());
        }else{
            System.err.println("crawl failed for " + url.toString() + ", error code: " + request.getResponseCode());
            failedUrls.add(url.toString());
        }
        timer.stop();
        System.out.println("crawled " + url.toString() + ", imposing crawl delay for " + crawlDelay + "ms");
        delayCrawl(timer.getElapsedTime());

    }

    private void delayCrawl(int elapsedTime){
        if(elapsedTime < crawlDelay){
            try {
                Thread.currentThread().sleep(crawlDelay - elapsedTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    //TODO move enqueue contains logic to robots
    private void processUrls(List<URL> retrievedUrls){
        for(URL url : retrievedUrls){
            if(isCrawlable(url)){
                String outboundHost = url.getProtocol() + "://" +  url.getHost();
                if(belongsToCurrentDomain(url)){
                    if(!isCrawled(url)){
                        if(!isDisallowed(url)){
                            pageQueue.enqueueUrl(url);
                        }else if(isDisallowed(url)){
                        }
                    }
                }else if(!discoveredDomains.containsURL(outboundHost) && !Crawler.domainHasBeenCrawled(url)){
                    discoveredDomains.enqueueUrl(outboundHost);
                }
            }

        }
    }

    public ArrayList<String> getDiscoveredDomains(){
        return discoveredDomains.toArrayList();
    }

    private boolean isCrawlable(URL url){
        boolean isCrawlable = true;
        if(url.getProtocol().contains("mailto")){
            isCrawlable = false;
        }
        return isCrawlable;
    }
    private boolean isCrawled(URL url){
        if(crawledUrls.size() > 0){
            for(int i  = 0 ; i  <= (crawledUrls.size() - 1) ; i++){
                if(crawledUrls.get(i).contains(url.toString())){
                    return true;
                }
            }
        }

        return false;
    }
    private boolean belongsToCurrentDomain(URL url) {
        try{
            return url.getHost().equals(domainURL.getHost());
        }catch( Exception e){
            System.out.println("lol");
        }

        return url.getHost().equals(domainURL.getHost());
    }
    private boolean isDisallowed(URL url){
        boolean disallowed = false;
        String path = url.getPath();
        for(String s : disallowedPaths){
            if(path.equals(s) || (!path.equals("/") && path.startsWith(s))){
                disallowed = true;
            }
        }
        return disallowed;
    }
    public int getQueueSize(){
        return this.pageQueue.getSize();
    }
    public String getDomainUrl(){
        return domainURL.toString();
    }
}
