package crawler;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.net.*;
import java.util.ArrayList;

public class Page{
    private Connection connection;
    private Document pageDocument;
    private URL url = null;
    private ArrayList<URL> crawlableUrls = new ArrayList<URL>();
    private Element head;
    private Element body;
    private String sourceCode;

    private static final org.apache.log4j.Logger errorLog = org.apache.log4j.Logger.getLogger("pageErrorLogger");

    public URL getUrl() {
        return url;
    }

    public Page(URL urlStr, String sourceCode){
        this.url = urlStr;
        this.sourceCode = sourceCode;
    }

    public void parseSource(){
        pageDocument = Jsoup.parse(sourceCode);
        head = pageDocument.head();
        body = pageDocument.body();
        //System.out.println("crawling: " + url + " " + Thread.currentThread().getId());
    }

    public ArrayList<URL> getCrawlableUrls(){
        // Remove urls with hashtags so the tomato doesn't add repeat pages to the queue that sneak in where the only difference in the url is the hashtag and it's ID
        try{
            pageDocument.select("a[href*=#]").remove();
        }catch(NullPointerException e){
            errorLog.warn("Thread interrupted " + "\n" + e.getStackTrace().toString());
        }

        for(Element anchor : pageDocument.select("a")){
            anchor.setBaseUri(url.toExternalForm());
            String hrefStr = anchor.attr("abs:href");
            URL href = null;
            if(hrefStr.length() > 0){
                try {
                    href = new URL(hrefStr);
                } catch (MalformedURLException e) {
                    errorLog.info("Malformed URL: " + hrefStr + "\n" + e.getStackTrace().toString());
                }
            }

            if(href != null){
                crawlableUrls.add(href);
            }
        }

        return crawlableUrls;
    }

    public String getSourceCode() {
        return sourceCode;
    }




}
