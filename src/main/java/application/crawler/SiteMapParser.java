package application.crawler;

import application.crawler.domain.Page;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class SiteMapParser {
    private String content;
    private Url url;
    private List<Url> urls = new ArrayList<Url>();
    private final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(this.getClass());

    public SiteMapParser(Url url, String content) {
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
                Url url = new Url(element.text());
                urls.add(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }
    private void parseContentAsWebpage(){
        Page page = new Page(url, content);
        urls = page.getDiscoveredPages();
    }
    public List<Url> getUrls(){
        return urls;
    }
}
