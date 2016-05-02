package application.crawler;

import application.crawler.domain.Page;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class SiteMapParser {
    private String content;
    private URI url;
    private List<URI> urls = new ArrayList<URI>();
    private final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(this.getClass());

    public SiteMapParser(URI url, String content) {
        this.content = content;
        this.url = url;
        parseContent();
    }

    private void parseContent(){
        if(url.getPath().contains(".xml") || content.trim().startsWith("<?xml")){
            parseContentAsXML();
        }else{
            parseContentAsWebpage();
        }
    }

    private void parseContentAsXML(){
        Document doc = Jsoup.parse(content, "", Parser.xmlParser());
        for (Element element : doc.getElementsByTag("loc")){
            try {
                URI url = new URI(element.text());
                urls.add(url);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    private void parseContentAsWebpage(){
        Page page = new Page(url, content);
        urls = page.getDiscoveredPages();
    }

    public List<URI> getURIs(){
        return urls;
    }
}
