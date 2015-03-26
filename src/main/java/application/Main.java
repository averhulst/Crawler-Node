package application;

import application.crawler.Crawler;

public class Main {
    public static void main(String[] args){
        //TODO identify likely optimal thread count, and then later on try and throttle thread pool size based on performance?
        Crawler crawler = new Crawler(2);
        crawler.crawl();
    }

}
