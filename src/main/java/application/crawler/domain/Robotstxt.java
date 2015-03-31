package application.crawler.domain;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Robotstxt {
    private boolean lineIsRelevant = true;
    private List<String> allowedPaths = new ArrayList<>();
    private List<String> disallowedPaths = new ArrayList<>();
    private List<String> disallowedSubPaths = new ArrayList<>();
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
        String[] splitLine = line.split(":");
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
                    case "allow":
                        allowedPaths.add(value);
                        break;
                    case "crawl-delay" :
                        crawlDelay = value.length() == 1 ? (Integer.parseInt(value) * 1000) : 5000;
                        //impose a maximum respected crawl delay of 5 seconds for now
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

    public List<String> getDisallowedPaths(){
        return disallowedPaths;
    }

    public List<String> getAllowedPaths() {
        return allowedPaths;
    }

    public boolean urlIsAllowed(URL url){
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
            urlIsAllowed = urlIsAllowed(new URL(url));
        } catch (MalformedURLException e) {
            //TODO log me
        }

        return urlIsAllowed;
    }

    private void parseCrawlDelay(String value){

    }
    public boolean crawlingIsProhibited(){
        return disallowedSubPaths.contains("/");
    }
}
