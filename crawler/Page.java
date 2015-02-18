package crawler;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.*;
import java.sql.Timestamp;
import java.util.ArrayList;

public class Page{
    private Connection connection;
    private Document pageDocument;
    private URL url = null;
    private ArrayList<URL> crawlableUrls = new ArrayList<URL>();
    private Element head;
    private Element body;
    private String sourceCode;

    public Page(String urlStr, String sourceCode){
        try {
            this.url = new URL(urlStr);
            this.sourceCode = sourceCode;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void parsePage(){
        pageDocument = Jsoup.parse(sourceCode);
        head = pageDocument.head();
        body = pageDocument.body();
        //System.out.println("crawling: " + url + " " + Thread.currentThread().getId());
    }

    public void insertPage(){

        //Where should page insert?
        //crawler.Crawler.getDao().insert("pageCrawls", dBObject);
    }

    public ArrayList<URL> getCrawlableUrls(){
        // Remove urls with hashtags so the tomato doesn't add repeat pages to the queue that sneak in where the only difference in the url is the hashtag and it's ID
        try{
            pageDocument.select("a[href*=#]").remove();
        }catch(NullPointerException npe){
            System.err.println(url);
            System.exit(717);
        }

        for(Element anchor : pageDocument.select("a")){
            anchor.setBaseUri(url.toExternalForm());
            String hrefStr = anchor.attr("abs:href");
            URL href = null;
            if(hrefStr.length() > 0){
                try {
                    href = new URL(hrefStr);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }


            if(href != null){
                crawlableUrls.add(href);
            }
        }


        return crawlableUrls;
    }

    private void logResponse(int statusCode){
        //Where should page log?
        //crawler.Crawler.getDao().insert("pageCrawls", dBObject);
    }
}
