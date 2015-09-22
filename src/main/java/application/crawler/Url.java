package application.crawler;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

public class Url {
    private URL url;

    public Url(String url) throws MalformedURLException {
        this.url = new URL(url);
    }

    public String getHost() {
        return url.getHost();
    }

    public String getAuthority() {
        return url.getAuthority();
    }

    public String getPath() {
        return url.getPath();
    }

    public String getProtocol() {
        return url.getProtocol();
    }

    public String getQuery() {
        return url.getQuery();
    }

    public boolean equals(Url url) {
        String otherBaseUrl = url.getProtocol() + url.getHost() + url.getPath();

        String baseUrl = this.url.getProtocol() + this.url.getHost() + this.url.getPath();

        return baseUrl.equals(otherBaseUrl) && queryStringIsEqual(url);
    }

    private boolean queryStringIsEqual(Url url) {
        Map<String, String> ourQueryMap = new LinkedHashMap<String, String>();
        Map<String, String> otherQueryMap = new LinkedHashMap<String, String>();
        String query = this.url.getQuery();
        String[] pairs = query.split("&");

        try {
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                    ourQueryMap.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
            }

            String otherQuery = url.getQuery();
            String[] otherPairs = query.split("&");

            for (String pair : otherPairs) {
                int idx = pair.indexOf("=");
                otherQueryMap.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return ourQueryMap.equals(otherQueryMap);

        // TODO I need to be tested!
    }
}
