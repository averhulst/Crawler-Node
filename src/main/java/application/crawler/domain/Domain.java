package application.crawler.domain;

import application.crawler.CrawlerSettings;
import application.crawler.Request;
import application.crawler.Url;
import application.crawler.util.Timer;
import application.crawler.UrlQueue;
import application.crawler.util.Util;
import org.json.JSONObject;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class Domain implements Runnable{
    private final int CRAWL_CEILING = CrawlerSettings.DOMAIN_PAGE_CRAWL_CEILING;

    private List<String> failedUrls;
    private List<Url> crawledUrls;
    private List<Url> discoveredDomains;
    private int crawlDelay;
    private long crawlStartTime;
    private boolean running;
    private Url domainURL;
    private RobotsTxt robotsTxt;
    private UrlQueue pageQueue;
    private Timer timer = new Timer();
    private int crawlCount;
    private JSONObject domainJson;
    private final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("domainLogger");

    public Domain(Url url) {
        this.domainURL = url;
        failedUrls = new ArrayList<>();
        crawledUrls = new ArrayList<>();
        discoveredDomains = new ArrayList<>();
        crawlStartTime = System.currentTimeMillis();
        pageQueue = new UrlQueue();
        pageQueue.enqueueUrl(url);

        domainJson = new JSONObject(){{
            put("url", domainURL);
            put("pages", new JSONObject());
        }};
        crawlCount = 0;
    }

    private void parseRobotsTxt(String pageSource){
        robotsTxt = new RobotsTxt(pageSource);
        crawlDelay = robotsTxt.getCrawlDelay();

        if(robotsTxt.hasSiteMap()){
            for(Url url : robotsTxt.getSiteMapUrls()){
                pageQueue.enqueueUrl(url);
            }
        }
    }

    public void run(){
        running = true;

        try {
            Url robotsTxtUrl =  new Url(domainURL + "/robots.txt");
            parseRobotsTxt(getPage(robotsTxtUrl).getSourceCode());
        } catch (MalformedURLException e) {
            System.err.println(domainURL.toString() + " no bueno");
            e.printStackTrace();
        }

        if(robotsTxt.crawlingIsProhibited()){
            return;
            //TODO handle this in a meaningful way
        }

        while(running){
            Url pageUrl = pageQueue.getNext();

            if(robotsTxt.urlIsAllowed(pageUrl)){
                Page page = getPage(pageUrl);
                parsePageContent(page);
                delayCrawl(timer.getElapsedTime());
            }
            crawlCount++;
            assertRunnable();
        }
    }

    private Page getPage(Url url){
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
            case 0   :
                //w3 spec indicates that the status attribute may return 0 if: If the state is UNSENT or OPENED, return 0 OR If the error flag is set, return 0.
                //other possible causes:
                //Illegal cross origin request (see CORS) - unlikely/impossible?
                //Firewall block or filtering
                //The request itself was cancelled in code
                logger.warn("Response of 0: " + request.getResponseCode() + " for " + url.toString() + "!");
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
        String pathHash = Util.toSha256(p.getUrl().getPath() + p.getUrl().getQuery());
        domainJson.getJSONObject("pages").put(pathHash, p.toJson());
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

    private void processDiscoveredDomains(List<Url> domains){
        for(Url url: domains){
            if(!discoveredDomains.contains(url)){
                discoveredDomains.add(url);
            }
        }
    }

    private void processDiscoveredPages(List<Url>  discoveredPages){
        for(Url url : discoveredPages){
            if(!isCrawled(url)){
                if(robotsTxt.urlIsAllowed(url)){
                    pageQueue.enqueueUrl(url);
                }
            }
        }
    }

    public List<Url> getDiscoveredDomains(){
        return discoveredDomains;
    }

    private boolean isCrawled(Url url){
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
    public String toJson() {
        return domainJson.toString();
    }
}
