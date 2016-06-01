package application.crawler.domain;

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
    private URI url;
    private Element head;
    private Element body;
    private String sourceCode;
    private List<URI> discoveredDomains;
    private List<URI> discoveredPages;

    private static final org.apache.log4j.Logger errorLog = org.apache.log4j.Logger.getLogger("pageLogger");

    public Page(URI urlStr, String sourceCode){
        this.url = urlStr;
        this.sourceCode = sourceCode;
        discoveredPages = new ArrayList<URI>();
        discoveredDomains = new ArrayList<URI>();

        parseSource();
        getCrawlableURIs();
    }

    public URI getURI() {
        return url;
    }

    public void parseSource(){
        pageDocument = Jsoup.parse(sourceCode);
        head = pageDocument.head();
        body = pageDocument.body();
    }

    public Element getBody() {
        return body;
    }

    private void getCrawlableURIs(){
        //TODO Remove urls with hashtags so the crawler doesn't add repeat pages to the queue that sneak in where the only difference in the url is the hashtag and it's ID
        try{
            pageDocument.select("a[href*=#]").remove();
        }catch(NullPointerException e){
            errorLog.warn("Thread interrupted " + "\n" + e.getStackTrace().toString());
        }

        for(Element anchor : pageDocument.select("a")){
            try {
                URI href =  processAnchor(anchor);

                if (href != null) {
                    parseURI(href);
                }

            } catch (URISyntaxException e) {
//                ignore these for now
//                e.printStackTrace();
                // TODO: improve this
            }
        }
    }

    public URI processAnchor(Element anchor) throws URISyntaxException {
        anchor.setBaseUri(url.getScheme() + "://" + url.getHost());
        //TODO reevaluate why we're setting baseURI, could possibly be creating invalid urls (pages that belong to other domains?) that i think we'll end up mistakenly crawling down the line
        String absoluteURIStr = anchor.attr("abs:href");

        if(absoluteURIStr.length() <= 0 ) {
            return null;
        }

        return new URI(absoluteURIStr);
    }

    public Boolean isLocalDomain(URI newURI) {
        return newURI.getHost() != null && newURI.getHost().equals(url.getHost());
    }

    public void parseURI(URI url) throws URISyntaxException {
        if(isLocalDomain(url)){
            discoveredPages.add(url);
        }else{
            URI newDomain = new URI(url.getScheme() + "://" + url.getHost());
            if(!discoveredDomains.contains(newDomain)){
                discoveredDomains.add(newDomain);
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

    public String toJson() {
        JSONObject page = new JSONObject();

        JSONObject source = new JSONObject(){{
            put("head", head.toString());
            put("body", body.toString());
        }};
        page.put(url.getPath() + url.getQuery(), source).toString();
        return page.toString();
    }




}
