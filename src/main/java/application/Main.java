package application;

import application.crawler.Crawler;
import org.apache.log4j.BasicConfigurator;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Main {
    public static void main(String[] args){
        int availableCpus = Runtime.getRuntime().availableProcessors();
        int crawlerWorkerThreadCount = availableCpus * 2 + 1;

        System.out.println(availableCpus + " processors found, using " + crawlerWorkerThreadCount + " worker threads for crawling");

        BasicConfigurator.configure();
        Crawler crawler = new Crawler(2);
        crawler.crawl();
    }

}
