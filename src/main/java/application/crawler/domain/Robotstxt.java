package application.crawler.domain;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Robotstxt {
    private boolean lineIsRelevant = true;
    private List<String> allowedPaths = new ArrayList<>();
    private List<String> disallowedPaths = new ArrayList<>();
    private List<String> disallowedSubPaths = new ArrayList<>();
    private List<URI> siteMaps = new ArrayList<>();
    private BufferedReader reader;
    private int crawlDelay = 500;

    private final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(this.getClass());

    public Robotstxt(String pageSource){
        this.reader = new BufferedReader(new StringReader(pageSource));
        parseRobotsTxt();
    }

    private void parseRobotsTxt(){
        String line;

        try {
            while((line = reader.readLine()) != null){
                if(!line.startsWith("#") && !line.isEmpty()){
                    parseLine(line);
                }
            }
        } catch (IOException e) {
            logger.warn("BufferedReader IOException " + "\n" + e.getStackTrace().toString());
        }
    }

    private void parseLine(String line){
        //okay this is becoming a mess, this needs to be refactored
        String[] splitLine = line.split(":", 2);
        String directive = splitLine[0].replaceAll("\\s", "");

        if(splitLine.length > 1){
            String value = splitLine[1].replaceAll("\\s", "");
            if(directive.toLowerCase().equals("user-agent")){
                lineIsRelevant = value.equals("*");
            }
            if(lineIsRelevant){
                switch (directive.toLowerCase()) {
                    case "disallow" :
                        if(value.endsWith("*")){
                            disallowedSubPaths.add(value.replace("*", ""));
                        }else{
                            disallowedPaths.add(value);
                        }
                        break;
                    case "allow" :
                        allowedPaths.add(value);
                        break;
                    case "crawl-delay" :
                        crawlDelay = value.length() == 1 ? (Integer.parseInt(value) * 1000) : 5000;
                        //impose a maximum respected crawl delay of 5 seconds for now
                        break;
                    case "sitemap":
                        try {
                            URI siteMap = new URL(value).toURI();
                            //initially creating URL instead of URI helps validate malformed URLs that the URI class would allow
                            siteMaps.add(siteMap);
                        } catch (MalformedURLException | URISyntaxException e) {
                            logger.warn("Error occurred while attempting to cast a URL from the sitemap directive. URL: " + value + ", Error: " + e.getStackTrace());
                        }

                        break;
                    default :
                        logger.warn("Unrecognized directive: " + directive);
                    break;
                }
            }
        }
    }

    public int getCrawlDelay(){
        return crawlDelay;
    }

    public boolean urlIsAllowed(URI url){
        String path = url.getPath();

        for(String disallowedSubPath : disallowedSubPaths){
            if(path.startsWith(disallowedSubPath)){
                for(String allowedPath : allowedPaths){
                    if(allowedPath.equals(path)){
                        return true;
                    }
                }
                return false;
            }
        }

        for(String disallowedPath : disallowedPaths){
            if(path.equals(disallowedPath)){
                return false;
            }
        }

        return true;
    }

    public boolean urlIsAllowed(String url){
        boolean urlIsAllowed = false;
        try {
            urlIsAllowed = urlIsAllowed(new URI(url));
        } catch (URISyntaxException e) {
            //TODO log me
        }

        return urlIsAllowed;
    }

    public boolean crawlingIsProhibited(){
        return disallowedSubPaths.contains("/");
    }

    public List<URI> getSiteMapUrls(){
        return siteMaps;
    }

    public boolean hasSiteMap(){
        return siteMaps != null;
    }
}