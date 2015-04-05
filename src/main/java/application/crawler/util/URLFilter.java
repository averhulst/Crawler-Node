package application.crawler.util;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class URLFilter {
    private final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(this.getClass());

    private final List<String> ALLOWED_PROTOCOLS = new ArrayList<String>(){{
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

    private boolean isExcluded(URI url){
        for(String fileType : FILE_TYPE_BLACKLIST){
            if(url.toString().endsWith(fileType)){
                return true;
            }
        }

        if(!ALLOWED_PROTOCOLS.contains(url.getScheme())){
            return true;
        }

        return false;
    }

    private boolean domainIsValid(URI url){
        if(url.toString().length() > 4){
            logger.error("URL length of 0 for url: " + url.toString() + " FIX ME!");
            return false;
        }
        if(url.getHost().length() == 0){
            logger.error("Host length of 0 for url: " + url.toString() + " FIX ME!");
            return false;
        }

        return true;
    }

    public boolean isCrawlable(URI url){
        return domainIsValid(url) && !isExcluded(url);
    }

}
