package application.crawler;

import application.crawler.domain.Domain;
import application.crawler.util.Util;
import org.apache.commons.lang3.StringUtils;
import service.messaging.MessengerImpl;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Crawler{
    private ExecutorService executor;
    private static UrlQueue domainQueue = new UrlQueue();
    private static HashMap<String, Domain> activelyCrawlingDomains = new HashMap<String, Domain>();
    private static long timeAtBootUp;
    private static float crawlRatePerMin;
    private static int totalCrawls;
    private int threadCount;
    private boolean running = true;
    private MessengerImpl messenger;
    private final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("crawlerLogger");
    private final org.apache.log4j.Logger fatalLogger = org.apache.log4j.Logger.getLogger("threadLogger");

    public Crawler(int threadCount) {
        this.threadCount = threadCount;
        messenger = new MessengerImpl();
        executor = Executors.newFixedThreadPool(threadCount);
        timeAtBootUp = System.currentTimeMillis();
        requestCrawlableDomains();
    }

    public synchronized void crawl(){

        while(running){
            if(domainQueue.getSize() > 0 && activelyCrawlingDomains.size() < threadCount){
                Domain newDomain = new Domain(
                    domainQueue.getNext()
                );
                activelyCrawlingDomains.put(newDomain.getDomainURI(), newDomain);
                executor.execute(new RunnableDomain(newDomain));
            }

            if(domainQueue.getSize() < 2){
                requestCrawlableDomains();
            }
        }
    }

    private void printCrawlRate(){
        float upTimeInSeconds = (int) (System.currentTimeMillis() - timeAtBootUp) / 1000;
        crawlRatePerMin = totalCrawls / (upTimeInSeconds / 60);
        logger.info("*******************  Total crawls:  " + totalCrawls + + Math.round(crawlRatePerMin*100.0)/100.0 + " domains per minute, running for " + upTimeInSeconds + " seconds. Actively crawling " + activelyCrawlingDomains.size() + " domains");

    }

    private void requestCrawlableDomains(){
        String freshDomainStr =  messenger.getQueue("freshDomains").getMessage();
        if(freshDomainStr.length() > 0){
            List<String> freshDomains = Arrays.asList(freshDomainStr.split(";"));
            for(String domain : freshDomains){
                domainQueue.enqueueURI(domain);
            }
        } else {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void finalizeDomainCrawl(Domain crawledDomain){
        activelyCrawlingDomains.remove(crawledDomain.getDomainURI());

        if(crawledDomain.getDiscoveredDomains().size() > 0){

            String discoveredDomainsMsg = StringUtils.join(crawledDomain.getDiscoveredDomains(),";");
            messenger.getQueue("discoveredDomains").publishMessage(discoveredDomainsMsg);
        }

        if(crawledDomain.toJson().length() != 0){
            String outBoundMessage = null;
            try {
                outBoundMessage = Util.compressString(crawledDomain.toJson());
            } catch (IOException e) {
                e.printStackTrace();
            }
            messenger.getQueue("crawlResults").setContentEncoding("gzip");
            messenger.getQueue("crawlResults").publishMessage(outBoundMessage);
        } else {
            System.out.println("wat");
        }

        logger.info("Finished crawling: " + crawledDomain.getDomainURI() + " - crawled " + crawledDomain.getCrawlCount() + " pages");
        totalCrawls++;
    }

    private class RunnableDomain implements Runnable{
        //TODO: remove me and refactor crawl to accept a Runnable lambda
        private Domain domain;

        public RunnableDomain(Domain newDomain){
            this.domain = newDomain;
        }

        public void run(){
            try{
                this.domain.run();
            }catch(Exception e){
                e.printStackTrace();
            }

            finalizeDomainCrawl(domain);
            printCrawlRate();
        }

    }


}