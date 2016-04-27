package application;

import application.crawler.Crawler;
import org.apache.log4j.BasicConfigurator;

public class Main {
    public static void main(String[] args){
        //int crawlerThreadCount = Integer.parseInt(args[0]);
        BasicConfigurator.configure();
        Crawler crawler = new Crawler(2);
        crawler.crawl();
    }

}
