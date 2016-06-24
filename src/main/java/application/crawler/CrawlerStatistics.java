package application.crawler;

import application.crawler.domain.Domain;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collection;

public class CrawlerStatistics {
    private float domainCrawlsPerMin;
    private int totalDomainCrawls = 0;
    private int upTimeInSeconds;
    private Collection<Domain> activelyCrawlingDomains;
    private String crawlerId;

    public CrawlerStatistics() {
        determineCrawlerId();
    }

    public String getCrawlerId() {
        return crawlerId;
    }

    public void setCrawlerId(String crawlerId) {
        this.crawlerId = crawlerId;
    }

    public float getDomainCrawlsPerMin() {
        return domainCrawlsPerMin;
    }

    public void setDomainCrawlsPerMin(float domainCrawlsPerMin) {
        this.domainCrawlsPerMin = domainCrawlsPerMin;
    }

    public int getTotalDomainCrawls() {
        return totalDomainCrawls;
    }

    public void setTotalDomainCrawls(int totalDomainCrawls) {
        this.totalDomainCrawls = totalDomainCrawls;
    }

    public int getUpTimeInSeconds() {
        return upTimeInSeconds;
    }

    public void setUpTimeInSeconds(int upTimeInSeconds) {
        this.upTimeInSeconds = upTimeInSeconds;
    }

    public Collection<Domain> getActivelyCrawlingDomains() {
        return activelyCrawlingDomains;
    }

    public void setActivelyCrawlingDomains(Collection<Domain> activelyCrawlingDomains) {
        this.activelyCrawlingDomains = activelyCrawlingDomains;
    }

    public String toString() {
        JSONObject response  = new JSONObject();

        response.put("domainCrawlsPerMin", domainCrawlsPerMin);
        response.put("totalDomainCrawls", totalDomainCrawls);
        response.put("upTimeInSeconds", upTimeInSeconds);
        response.put("crawlerId", crawlerId);

        JSONArray activelyCrawlingDomainsJson = new JSONArray();

        for (Domain d : activelyCrawlingDomains) {
            activelyCrawlingDomainsJson.put(d.getDomainURI());
        }

        response.put("activelyCrawlingDomains", activelyCrawlingDomainsJson);

        return response.toString();
    }

    private void determineCrawlerId() {
        try {
            NetworkInterface network = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
            StringBuilder sb = new StringBuilder();

            byte[] mac = network.getHardwareAddress();

            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            }

            crawlerId = sb.toString();

        } catch (UnknownHostException | SocketException e) {
            e.printStackTrace();
        }
    }

}
