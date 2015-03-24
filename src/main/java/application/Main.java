package application;

import application.crawler.Crawler;

public class Main {
    public static void main(String[] args){
        Crawler crawler = new Crawler(2);
        crawler.crawl();
    }

}
