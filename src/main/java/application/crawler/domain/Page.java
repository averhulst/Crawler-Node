package application.crawler.domain;

import application.crawler.Url;
import application.crawler.util.Util;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Page{
    private Document pageDocument;
    private Url url;
    private Element head;
    private Element body;
    private String sourceCode;
    private List<Url> discoveredDomains;
    private List<Url> discoveredPages;

    private static final org.apache.log4j.Logger errorLog = org.apache.log4j.Logger.getLogger("pageLogger");

    public Page(Url urlStr, String sourceCode){
        this.url = urlStr;
        this.sourceCode = sourceCode;
        discoveredPages = new ArrayList<Url>();
        discoveredDomains = new ArrayList<Url>();

        parseSource();
        getCrawlableUrls();
    }

    public Url getUrl() {
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
            try {
                Url href =  processAnchor(anchor);
                parseUrl(href);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    public Url processAnchor(Element anchor) throws MalformedURLException {
        anchor.setBaseUri(url.getProtocol() + "://" + url.getHost());
        //TODO reevaluate why we're setting baseUrl, could possibly be creating invalid urls (pages that belong to other domains?) that i think we'll end up mistakenly crawling down the line
        String absoluteUrlStr = anchor.attr("abs:href");

        if(absoluteUrlStr.length() <= 0 ) {
            throw new RuntimeException("Investigate me, why is urlStrLength less than 0 for " + anchor.toString());
        }

        return new Url(absoluteUrlStr);
    }

    public Boolean isLocalDomain(Url newUrl) {
        return newUrl.getHost() != null && newUrl.getHost().equals(url.getHost());
    }

    public void parseUrl(Url url) throws MalformedURLException {
        if(isLocalDomain(url)){
            discoveredPages.add(url);
        }else{
            Url newDomain = new Url(url.getProtocol() + "://" + url.getHost());
            if(!discoveredDomains.contains(newDomain)){
                discoveredDomains.add(newDomain);
            }
        }
    }

    public List<Url> getDiscoveredDomains(){
        return discoveredDomains;
    }

    public List<Url> getDiscoveredPages(){
        return discoveredPages;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public String toJson() {
        JSONObject page = new JSONObject();

        JSONObject source = new JSONObject(){{
            put("head", head.toString());
            put("body", body.toString());
        }};
        page.put(Util.toSha256(url.getPath() + url.getQuery()), source).toString();
        return page.toString();
    }




}
