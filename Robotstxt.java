import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Robotstxt {
    private boolean hasRobotsTxt = false;
    private boolean lineRelevance = true;
    private URL domainUrl;
    private ArrayList<String> allowedPaths = new ArrayList<String>();
    private ArrayList<String> disallowedPaths = new ArrayList<String>();
    private BufferedReader reader;
    private int crawlDelay = 500;
    private String line;

    public Robotstxt(URL domain){
        URL robotsUrl = null;
        try {
            robotsUrl = new URL(domain.getProtocol() + "://" + domain.getHost() + "/robots.txt");
        } catch (MalformedURLException e) {
            System.err.println("Invalid URL provided to robotsTxt parser");
            e.printStackTrace();
        }

        this.domainUrl = robotsUrl;
    }

    public void fetch(){
        try {
            InputStreamReader isr = new InputStreamReader(domainUrl.openStream());
            reader = new BufferedReader(isr);
            hasRobotsTxt = true;
        } catch (FileNotFoundException e) {
            //no robots.txt
            System.err.println("no robots file " + domainUrl.toString());
            //TODO: log this
        } catch (IOException e) {
            System.err.println("unable to read robots.txt from " + domainUrl.toString());
            e.printStackTrace();
        }

        if(hasRobotsTxt){
            parseRobotsTxt();
        }
    }

    public void parseRobotsTxt(){
        try {
            while((line = reader.readLine()) != null && !line.isEmpty() && !line.startsWith("#")){

                String[] linePart = line.split(":");
                String directive = linePart[0].replaceAll("\\s", "");

                if(linePart.length > 1){
                    String value = linePart[1].replaceAll("\\s", "");
                    if(directive.equals("User-agent")){
                        if((value.equals("*") || value.equals("Tomato"))){
                            lineRelevance = true;
                        }else{
                            lineRelevance = false;
                        }
                    }
                    if(lineRelevance){
                        if(directive.equals("Disallow")){
                            disallowedPaths.add(value);
                        }else if (directive.equals("Allow")){
                            allowedPaths.add(value);
                        }else if(directive.equals("Crawl-delay") && Integer.parseInt(value) > 0){
                            crawlDelay = Integer.parseInt(value) * 1000;
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }catch(NullPointerException e){
            e.printStackTrace();
        }catch(ArrayIndexOutOfBoundsException e){
            System.err.println(e.toString());
            e.printStackTrace();
        }
    }

    public int getCrawlDelay(){
        return crawlDelay;
    }
    public ArrayList<String> getDisallowedPaths(){
        return disallowedPaths;
    }
}
