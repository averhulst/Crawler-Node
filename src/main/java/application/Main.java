package application;

import application.crawler.Crawler;
import org.apache.log4j.BasicConfigurator;

public class Main {
    public static void main(String[] args){
        BasicConfigurator.configure();
        //TODO identify likely optimal thread count, and then later on try and throttle thread pool size based on performance?
        Crawler crawler = new Crawler(2);
        crawler.crawl();
    }

}
