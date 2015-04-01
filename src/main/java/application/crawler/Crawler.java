package application.crawler;

import application.crawler.domain.Domain;
import application.crawler.util.UrlQueue;
import service.messaging.MessagingService;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Crawler{
    private ExecutorService executor;
    private static UrlQueue domainQueue = new UrlQueue();
    private static HashMap<String, Domain> activelyCrawlingDomains = new HashMap<String, Domain>();
    private static UrlQueue crawledDomains = new UrlQueue();
    //TODO remove crawledDomains functionality, responsibility belongs to crawler hub now
    private static long timeAtBootUp;
    private static float crawlRatePerMin;
    private static int totalCrawls;
    private int threadCount;
    private boolean running = true;
    private MessagingService messenger;
    private final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(this.getClass() + "_INFO");
    private final org.apache.log4j.Logger errorLog = org.apache.log4j.Logger.getLogger(this.getClass() + "_ERROR");

    public Crawler(int threadCount) {
        this.threadCount = threadCount;
        messenger = new MessagingService();
        executor = Executors.newFixedThreadPool(threadCount);
        timeAtBootUp = System.currentTimeMillis();
        requestCrawlableDomains();
    }

    public synchronized void crawl(){
        String newUrl = new String();
        while(running){
            if(domainQueue.getSize() > 0 && activelyCrawlingDomains.size() < threadCount){
                try {
                    newUrl = domainQueue.getNext();
                    Domain newDomain = new Domain(
                            new URL(newUrl)
                    );
                    activelyCrawlingDomains.put(newDomain.getDomainUrl(), newDomain);
                    executor.execute(new RunnableDomain(newDomain));
                } catch (MalformedURLException e) {
                    errorLog.warn("Malformed URL: " + newUrl + "\n" + e.getStackTrace().toString());
                }
            }

            if(domainQueue.getSize() < 2){
                requestCrawlableDomains();
            }
        }
    }

    private void printCrawlRate(){
        float upTimeInSeconds = (int) (System.currentTimeMillis() - timeAtBootUp) / 1000;
        crawlRatePerMin = totalCrawls / (upTimeInSeconds / 60);
        logger.info(crawlRatePerMin + " domains per minute" + " Total crawls: " + totalCrawls + " running for " + upTimeInSeconds / 1000 + " seconds");
    }

    private void requestCrawlableDomains(){
        List<String> newDomains = messenger.fetchFreshDomains();
        for(String domain : newDomains){
            domainQueue.enqueueUrl(domain);
        }
    }

    private void finalizeDomainCrawl(Domain crawledDomain){
        activelyCrawlingDomains.remove(crawledDomain.getDomainUrl());
        crawledDomains.enqueueUrl(crawledDomain.getDomainUrl());

        if(crawledDomain.getDiscoveredDomains().size() > 0){
            List<URL> discoveredDomains = crawledDomain.getDiscoveredDomains();
            //messenger.publishMessage(String.join(";", discoveredDomains));
            messenger.publishDiscoveredDomains(discoveredDomains);

        }
        logger.info("Finished crawling: " + crawledDomain.getDomainUrl() + " - crawled " + crawledDomain.getCrawlCount() + " pages");
        totalCrawls++;
    }

    private class RunnableDomain implements Runnable{
        //TODO: remove me and refactor crawl to accept a Runnable lambda
        private Domain domain;

        public RunnableDomain(Domain newDomain){
            this.domain = newDomain;
        }

        public void run(){
            this.domain.run();
            finalizeDomainCrawl(domain);
            printCrawlRate();
        }

    }


}