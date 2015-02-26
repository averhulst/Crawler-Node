package crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Request {
    private String response = "";
    private URL pageUrl;
    private int responseCode;
    private HttpURLConnection urlConnection;

    public Request(URL pageUrl) {
        this.pageUrl = pageUrl;

        try {
            connect();
            processConnection();
        } catch (IOException e) {
            System.exit(20);
        }
    }

    private void connect() throws IOException{
        //URLConnection urlConnection = pageUrl.openConnection();
        urlConnection = (HttpURLConnection)pageUrl.openConnection();
        urlConnection.setConnectTimeout(5000);
        urlConnection.setRequestMethod("GET");

    }

    private void processConnection() throws IOException{
        responseCode = urlConnection.getResponseCode();
        String message = urlConnection.getResponseMessage();

        if(responseCode == 200){
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            urlConnection.getInputStream()
                    ));

            String line;

            while ((line = reader.readLine()) != null){
                response += line;
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
}