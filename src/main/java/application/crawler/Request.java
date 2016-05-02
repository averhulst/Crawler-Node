package application.crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

public class Request {
    private String response = "";
    private URL pageURL;
    private int responseCode;
    private HttpURLConnection urlConnection;
    private String requestMethod = "GET";
    private int connectionTimeout = 1000;
    private String userAgent = "";

    public Request(URL pageURL) {
        this.pageURL = pageURL;
    }

    public void connect() throws IOException{
        //URLConnection urlConnection = pageURL.openConnection();
        urlConnection = (HttpURLConnection)pageURL.openConnection();
        urlConnection.setConnectTimeout(connectionTimeout);
        urlConnection.setRequestMethod(requestMethod);
        urlConnection.setRequestProperty("User-Agent", userAgent);
        readConnection();
    }

    private void readConnection() throws IOException{
        responseCode = urlConnection.getResponseCode();
        String message = urlConnection.getResponseMessage();

        if(responseCode == 200){
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            urlConnection.getInputStream()
                    ));

            String line;

            while ((line = reader.readLine()) != null){
                response += line + "\n";
            }
            reader.close();
        }else{
            response = message;
        }
    }

    public int getResponseCode(){
        return responseCode;
    }

    public String getResponse(){
        return response;
    }

    public void setUserAgent(String useragent) {
        this.userAgent = useragent;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }
}