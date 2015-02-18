package crawler;

import messaging.MessagingService;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Crawler{
    private ExecutorService executor;
    private static UrlQueue domainQueue = new UrlQueue();
    private static HashMap<String, Domain> activelyCrawlingDomains = new HashMap<String, Domain>();
    private static UrlQueue crawledDomains = new UrlQueue();
    private static long timeAtBootUp;
    private static float lifeTime;
    private static float crawlRate;
    private static int totalCrawls;
    private int threadCount;
    private static float crawlRateTemp;
    private boolean running = true;
    private MessagingService messenger;

    public Crawler(int threadCount) {
        this.threadCount = threadCount;
        messenger = new MessagingService();
        executor = Executors.newFixedThreadPool(threadCount);
        //dao = MongoDao.getInstance();
        timeAtBootUp = System.currentTimeMillis();

        List<String> domainSeed = new ArrayList<>();

        domainSeed.add("http://animagraffs.com/");
        domainSeed.add("http://jgrapht.org/");
        domainSeed.add("http://www.pixijs.com/resources/");
        domainSeed.add("http://www.draw2d.org/draw2d/");

        messenger.publishDiscoveredDomains(domainSeed);
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
                    e.printStackTrace();
                }
            }

            if(domainQueue.getSize() < 2){
                //requestCrawlableDomains();
            }
        }
    }

    public static synchronized boolean domainIsQueued(URL url){
        return domainQueue.containsURL(url.getProtocol() + "://" + url.getHost());
    }

    public static synchronized boolean domainHasBeenCrawled(URL url){
        return crawledDomains.containsURL(url.getProtocol() + "://" + url.getHost());
    }

    private static void printCrawlRate(){
        lifeTime = (int) (System.currentTimeMillis() - timeAtBootUp);
        crawlRate = totalCrawls/(lifeTime/60000);
        System.out.println("Crawl rate: " + crawlRate + " crawls per minute" + " Total crawls: " + totalCrawls + " running for " + lifeTime / 1000 + " seconds");
    }

    private void requestCrawlableDomains(){
        for(int i = 0 ; i < 2 ; i++){
            List newDomains = messenger.fetchFreshDomains();
            domainQueue.enqueueUrl(newDomains.get(0).toString());
        }
    }

    private void finalizeDomainCrawl(Domain crawledDomain){
        activelyCrawlingDomains.remove(crawledDomain.getDomainUrl());
        crawledDomains.enqueueUrl(crawledDomain.getDomainUrl());

        if(crawledDomain.getDiscoveredDomains().size() > 0){
            ArrayList<String> discoveredDomains = crawledDomain.getDiscoveredDomains();
            //messenger.publishMessage(String.join(";", discoveredDomains));
            messenger.publishDiscoveredDomains(discoveredDomains);
        }

        totalCrawls++;
        crawlRateTemp++;

        if(crawlRateTemp > 10 && totalCrawls > 0){
            printCrawlRate();
            crawlRateTemp = 0;
        }else{
            crawlRateTemp++;
        }
    }

    private class RunnableDomain implements Runnable{
        private Domain domain;

        public RunnableDomain(Domain newDomain){
            this.domain = newDomain;
        }

        public void run(){
            this.domain.run();
            finalizeDomainCrawl(domain);
        }

    }


}