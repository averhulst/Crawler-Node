package application.crawler.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class URLFilter {
    private final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(this.getClass());

    private final List<String> PROTOCOL_WHITELIST = new ArrayList<String>(){{
        add("http");
        add("https");
    }};

    private final List<String> FILE_TYPE_BLACKLIST =
        new ArrayList<>(
            Arrays.asList(
                    new String[]{
                            ".jpg", ".jpeg", ".png", ".tiff",
                            ".gif", ".rif", ".bmp", ".pdf",
                            ".doc", ".js", ".css"
                    }
            )
    );

    public boolean pageIsCrawlable(URL url){
        if(!domainIsCrawlable(url)){
            return false;
        }

        for(String fileType : FILE_TYPE_BLACKLIST){
            if(url.toString().endsWith(fileType)){
                return false;
            }
        }
        return true;
    }
    public boolean domainIsCrawlable(URL url){
        boolean crawlable = true;

        if(!PROTOCOL_WHITELIST.contains(url.getProtocol())){
            crawlable = false;
        }

        if(url.getHost().length() == 0){
            logger.error("Host length of 0 for url: " + url.toString() + " FIX ME!");
            crawlable = false;
        }
        return crawlable;
    }

}
