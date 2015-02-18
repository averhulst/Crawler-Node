import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Request {
    private String response = "";
    private URL pageUrl;
    private int responseCode;
    private String reponseMessage;
    BufferedReader in;
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
        urlConnection.setConnectTimeout(5000);
        urlConnection.setRequestMethod("GET");
        urlConnection = (HttpURLConnection)pageUrl.openConnection();
    }

    private void processConnection() throws IOException{
        responseCode = urlConnection.getResponseCode();
        reponseMessage = urlConnection.getResponseMessage();

        if(responseCode == 200){
            in = new BufferedReader(
                    new InputStreamReader(
                            urlConnection.getInputStream()
                    ));
            String inputLine;

            while ((inputLine = in.readLine()) != null){
                response += inputLine;
            }
            in.close();
        }else{
            response = reponseMessage;
        }


    }

    public int getResponseCode(){
        return responseCode;
    }

    public String getResponse(){
        return response;
    }
}