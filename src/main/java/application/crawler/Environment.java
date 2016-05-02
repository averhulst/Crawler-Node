package application.crawler;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.*;

public class Environment {
    public static final String CRAWL_RESULTS_DB_ADDRESS;
    public static final int    CRAWL_RESULTS_DB_PORT;

    public static final String DOMAIN_QUEUE_DB_ADDRESS;
    public static final int    DOMAIN_QUEUE_DB_PORT;

    public static final String MESSAGING_SERVICE_ADDRESS;
    public static final int    MESSAGING_SERVICE_PORT;
    public static final String MESSAGING_SERVICE_USER_NAME;
    public static final String MESSAGING_SERVICE_PASS;

    static {
        String configStr = null;
        InputStream inputStream = Environment.class.getClassLoader().getResourceAsStream("environment_config.txt");

        try {
            configStr = IOUtils. toString(inputStream, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject environmentJSON = new JSONObject(configStr.toString());

        CRAWL_RESULTS_DB_ADDRESS    =  environmentJSON.getJSONObject("CRAWL_RESULTS_DB").getString("Address");
        CRAWL_RESULTS_DB_PORT       =  (int)environmentJSON.getJSONObject("CRAWL_RESULTS_DB").get("Port");
        DOMAIN_QUEUE_DB_ADDRESS     =  environmentJSON.getJSONObject("DOMAIN_QUEUE_DB").getString("Address");
        DOMAIN_QUEUE_DB_PORT        =  (int)environmentJSON.getJSONObject("DOMAIN_QUEUE_DB").get("Port");
        MESSAGING_SERVICE_ADDRESS   =  environmentJSON.getJSONObject("MESSAGING_SERVICE").getString("Address");
        MESSAGING_SERVICE_PORT      =  (int)environmentJSON.getJSONObject("MESSAGING_SERVICE").get("Port");
        MESSAGING_SERVICE_USER_NAME   =  environmentJSON.getJSONObject("MESSAGING_SERVICE").getString("User_name");
        MESSAGING_SERVICE_PASS      =  environmentJSON.getJSONObject("MESSAGING_SERVICE").getString("Pass");

    }

}
