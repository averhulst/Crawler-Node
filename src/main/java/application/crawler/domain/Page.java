package application.crawler.domain;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Page{
    private Connection connection;
    private Document pageDocument;
    private URL url = null;
    private Element head;
    private Element body;
    private String sourceCode;
    private List<String> discoveredDomains;
    private List<URL> discoveredPages;

    private static final org.apache.log4j.Logger errorLog = org.apache.log4j.Logger.getLogger("pageErrorLogger");

    public Page(URL urlStr, String sourceCode){
        this.url = urlStr;
        this.sourceCode = sourceCode;
        discoveredPages = new ArrayList<URL>();
        discoveredDomains = new ArrayList<String>();

        parseSource();
        getCrawlableUrls();
    }

    public URL getUrl() {
        return url;
    }
    public void parseSource(){
        pageDocument = Jsoup.parse(sourceCode);
        head = pageDocument.head();
        body = pageDocument.body();
    }

    private void getCrawlableUrls(){
        //TODO Remove urls with hashtags so the tomato doesn't add repeat pages to the queue that sneak in where the only difference in the url is the hashtag and it's ID
        try{
            pageDocument.select("a[href*=#]").remove();
        }catch(NullPointerException e){
            errorLog.warn("Thread interrupted " + "\n" + e.getStackTrace().toString());
        }

        for(Element anchor : pageDocument.select("a")){
            anchor.setBaseUri(url.toExternalForm());
            //TODO reevaluate why we're setting baseURI, could possibly be creating invalid urls (pages that belong to other domains?) that i think we'll end up mistakenly crawling down the line
            String absoluteUrlStr = anchor.attr("abs:href");

            if(absoluteUrlStr.length() > 0){
                try {
                    URL href = new URL(absoluteUrlStr);

                    if(href.getHost().equals(url.getHost())){
                        discoveredPages.add(href);
                    }else{
                        discoveredDomains.add(absoluteUrlStr.toString());
                    }
                } catch (MalformedURLException e) {
                    errorLog.info("Malformed URL: " + absoluteUrlStr + "\n" + e.getStackTrace().toString());
                }
            }
        }
    }

    public List<String> getDiscoveredDomains(){
        return discoveredDomains;
    }

    public List<URL> getDiscoveredPages(){
        return discoveredPages;
    }

    public String getSourceCode() {
        return sourceCode;
    }




}
