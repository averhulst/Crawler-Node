package application.crawler.domain;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class ParsedURL {

    private URL url;

    public ParsedURL(String urlStr) throws MalformedURLException {
        this.url = new URL(urlStr);
    }

    public String getHost() {
        return url.getHost();
    }

    public int getPort() {
        return url.getPort();
    }

    public String getPath() {
        return url.getPath();
    }

    public String getAuthority() {
        return url.getAuthority();
    }

    public String getQuery() {
        return url.getQuery();
    }

    public boolean equals(ParsedURL otherUrl) {
        return url.toString().equals(otherUrl.toString());
    }

    public int hashCode() {
        return url.toString().hashCode();
    }
}
