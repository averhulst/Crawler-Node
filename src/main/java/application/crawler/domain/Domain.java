package application.crawler.domain;

import application.crawler.util.Request;
import application.crawler.util.Timer;
import application.crawler.util.UrlQueue;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class Domain implements Runnable{
    private final int CRAWL_CEILING = 25;

    private List<String> failedUrls;
    private List<URI> crawledUrls;
    private List<URI> discoveredDomains;
    private int crawlDelay;
    private long crawlStartTime;
    private boolean running;
    private URI domainURL;
    private Robotstxt robotsTxt;
    private UrlQueue pageQueue;
    private Timer timer = new Timer();
    private int crawlCount;
    private JSONObject domainJson;
    private final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(this.getClass());

    public Domain(URI url) {
        this.domainURL = url;
        failedUrls = new ArrayList<>();
        crawledUrls = new ArrayList<>();
        discoveredDomains = new ArrayList<>();
        crawlStartTime = System.currentTimeMillis();
        pageQueue = new UrlQueue();
        pageQueue.enqueueUrl(url);

        domainJson = new JSONObject(){{
            put("url", domainURL);
            put("pages", new JSONArray());
        }};
        crawlCount = 0;
    }

    private void parseRobotsTxt(String pageSource){
        robotsTxt = new Robotstxt(pageSource);
        crawlDelay = robotsTxt.getCrawlDelay();
    }

    public void run(){
        running = true;

        try {
            URI robotsTxtUrl =  new URI(domainURL + "robots.txt");
            parseRobotsTxt(getPage(robotsTxtUrl).getSourceCode());
        } catch (URISyntaxException e) {
            logger.error("Malformed URL for robots.txt: " + e.getStackTrace().toString());
        }

        if(robotsTxt.crawlingIsProhibited()){
            return;
            //TODO handle this in a meaningful way
        }

        while(running){
            URI pageUrl = pageQueue.getNext();

            if(robotsTxt.urlIsAllowed(pageUrl)){
                Page page = getPage(pageUrl);
                parsePageContent(page);
                delayCrawl(timer.getElapsedTime());
            }
            crawlCount++;
            assertRunnable();
        }
    }

    private Page getPage(URI url){
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
        domainJson.getJSONArray("pages").put(p.toJson());
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

    private void processDiscoveredDomains(List<URI> domains){
        for(URI url: domains){
            if(!discoveredDomains.contains(url)){
                discoveredDomains.add(url);
            }
        }
    }

    private void processDiscoveredPages(List<URI>  discoveredPages){
        for(URI url : discoveredPages){
            if(!isCrawled(url)){
                if(robotsTxt.urlIsAllowed(url)){
                    pageQueue.enqueueUrl(url);
                }
            }
        }
    }

    public List<URI> getDiscoveredDomains(){
        return discoveredDomains;
    }

    private boolean isCrawled(URI url){
        if(crawledUrls.size() > 0){
            return crawledUrls.contains(url.toString());
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
    public JSONObject toJson() {
        return domainJson;
    }
}
