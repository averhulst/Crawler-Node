package application.crawler.domain;

import application.crawler.util.Request;
import application.crawler.util.Timer;
import application.crawler.util.URLFilter;
import application.crawler.util.UrlQueue;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class Domain implements Runnable{
    private List<String> failedUrls;
    private List<URL> crawledUrls;
    private List<URL> discoveredDomains;
    private int crawlDelay;
    private long crawlStartTime;
    private boolean running;
    private URL domainURL;
    private Robotstxt robotsTxt;
    private UrlQueue pageQueue;
    private Timer timer = new Timer();
    private int crawlCount;
    private URLFilter urlFilter;
    private final int CRAWL_CEILING = 25;

    private final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(this.getClass());

    public Domain(URL url) {
        this.domainURL = url;
        failedUrls = new ArrayList<>();
        crawledUrls = new ArrayList<>();
        discoveredDomains = new ArrayList<>();
        crawlStartTime = System.currentTimeMillis();
        pageQueue = new UrlQueue();
        pageQueue.enqueueUrl(url);
        urlFilter = new URLFilter();
        crawlCount = 0;
    }

    private void parseRobotsTxt(String pageSource){
        robotsTxt = new Robotstxt(pageSource);
        crawlDelay = robotsTxt.getCrawlDelay();
    }

    public void run(){
        running = true;

        try {
            URL robotsTxtUrl =  new URL(domainURL + "robots.txt");
            parseRobotsTxt(getPage(robotsTxtUrl).getSourceCode());
        } catch (MalformedURLException e) {
            logger.warn("Malformed URL for robots.txt: " + e.getStackTrace().toString());
        }

        if(robotsTxt.crawlingIsProhibited()){
            return;
            //TODO handle this in a meaningful way
        }

        while(running){
            String pageUrl = pageQueue.getNext();
            try {
                if(robotsTxt.urlIsAllowed(pageUrl)){
                    Page page = getPage(new URL(pageUrl));
                    parsePageContent(page);
                    delayCrawl(timer.getElapsedTime());
                }
                crawlCount++;
            } catch (Exception e) {
                logger.warn("Crawl failed for: " + pageUrl + "\n" + e.getStackTrace().toString());
            }
            assertRunnable();
        }
    }

    private Page getPage(URL url){
        Request request = new Request(url);
        request.setConnectionTimeout(5000);
        request.setRequestMethod("GET");
        request.setUserAgent("Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

        try{
            request.connect();
        }catch(IOException e){
            logger.warn("unable to connect to: " + url + "\n" + e.getStackTrace().toString());
        }

        Page page = new Page(url, request.getResponse());

        switch (request.getResponseCode()) {
            case 200 :
                break;
            case 301:
            case 302:
            case 303:
                logger.info(request.getResponseCode() +  " REDIRECT: " + url.toString() + "\n");
                //TODO: work out redirection
            case 404 :
                logger.info("404 NOT FOUND: " + url.toString() + "\n");
                failedUrls.add(url.toString());
                //TODO: log this
                break;
            case 403 :
                logger.info("403 FORBIDDEN: " + url.toString() + "\n");
                logger.info("Request failure for: " + url.toString() + ", response code: " + request.getResponseCode());
                failedUrls.add(url.toString());
                break;
            default :
                logger.warn("Unrecognized response code: " + request.getResponseCode() + " for " + url.toString());
        }

        return page;
    }

    private void parsePageContent(Page p){
        p.parseSource();
        processDiscoveredDomains(p.getDiscoveredDomains());
        processDiscoveredPages(p.getDiscoveredPages());
        crawledUrls.add(p.getUrl());
    }

    private void delayCrawl(int elapsedTime){
        if(elapsedTime < crawlDelay){
            try {
                Thread.currentThread().sleep(crawlDelay - elapsedTime);
            } catch (InterruptedException e) {
                logger.warn("Thread interrupted " + "\n" + e.getStackTrace().toString());
                e.printStackTrace();
            }
        }
    }

    private void processDiscoveredDomains(List<URL> domains){
        for(URL url: domains){
            if(!discoveredDomains.contains(url) && urlFilter.domainIsCrawlable(url)){
                discoveredDomains.add(url);
            }
        }
    }

    private void processDiscoveredPages(List<URL>  discoveredPages){
        for(URL url : discoveredPages){
            if(!isCrawled(url) && urlFilter.pageIsCrawlable(url)){
                if(robotsTxt.urlIsAllowed(url)){
                    pageQueue.enqueueUrl(url);
                }
            }
        }
    }

    public List<URL> getDiscoveredDomains(){
        return discoveredDomains;
    }

    private boolean isCrawled(URL url){
        if(crawledUrls.size() > 0){
            for(int i  = 0 ; i  <= (crawledUrls.size() - 1) ; i++){
                if(crawledUrls.get(i).toString().contains(url.toString())){
                    return true;
                }
            }
        }
        return false;
    }

    public String getDomainUrl(){
        return domainURL.toString();
    }
    private void assertRunnable(){
        if(crawlCount >= CRAWL_CEILING){
            running = false;
        }
        if(!pageQueue.hasNext()){
            logger.debug("Domain:" + domainURL + " page queue emptied after " + crawlCount + " page crawls");
            running = false;
        }
    }
    private long getElapsedUpTimeMs(){
        return (System.currentTimeMillis() - crawlStartTime);
        //TODO expose more meta data methods & send up with crawl results
    }
    public int getCrawlCount(){
        return crawlCount;
    }
}
