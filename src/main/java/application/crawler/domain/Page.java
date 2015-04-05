package application.crawler.domain;

import org.json.JSONObject;
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
    private URI url;
    private Element head;
    private Element body;
    private String sourceCode;
    private List<URI> discoveredDomains;
    private List<URI> discoveredPages;

    private static final org.apache.log4j.Logger errorLog = org.apache.log4j.Logger.getLogger("pageErrorLogger");

    public Page(URI urlStr, String sourceCode){
        this.url = urlStr;
        this.sourceCode = sourceCode;
        discoveredPages = new ArrayList<URI>();
        discoveredDomains = new ArrayList<URI>();

        parseSource();
        getCrawlableUrls();
    }

    public URI getUrl() {
        return url;
    }
    public void parseSource(){
        pageDocument = Jsoup.parse(sourceCode);
        head = pageDocument.head();
        body = pageDocument.body();
    }

    private void getCrawlableUrls(){
        //TODO Remove urls with hashtags so the crawler doesn't add repeat pages to the queue that sneak in where the only difference in the url is the hashtag and it's ID
        try{
            pageDocument.select("a[href*=#]").remove();
        }catch(NullPointerException e){
            errorLog.warn("Thread interrupted " + "\n" + e.getStackTrace().toString());
        }

        for(Element anchor : pageDocument.select("a")){
            anchor.setBaseUri(url.getScheme() + "://" + url.getHost());
            //TODO reevaluate why we're setting baseURI, could possibly be creating invalid urls (pages that belong to other domains?) that i think we'll end up mistakenly crawling down the line
            String absoluteUrlStr = anchor.attr("abs:href");
            if(absoluteUrlStr.length() > 0){
                try {
                    URI href = new URI(absoluteUrlStr);
                    if(href.getHost().equals(url.getHost())){
                        discoveredPages.add(href);
                    }else{
                        discoveredDomains.add(new URI(href.getScheme() + "://" + href.getHost()));
                    }
                } catch (URISyntaxException e) {
                    errorLog.info("Malformed URL: " + absoluteUrlStr + "\n" + e.getStackTrace().toString());
                }
            }
        }
    }

    public List<URI> getDiscoveredDomains(){
        return discoveredDomains;
    }

    public List<URI> getDiscoveredPages(){
        return discoveredPages;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public JSONObject toJson() {
        JSONObject page = new JSONObject();

        JSONObject source = new JSONObject(){{
            put("head", head.toString());
            put("body", body.toString());
        }};
        page.put(url.toString(), source);
        return page;
    }




}
