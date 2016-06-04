package application.crawler;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationFactory;
import org.apache.commons.configuration.DefaultConfigurationBuilder;

public class CrawlerSettings {
    public static final int DOMAIN_PAGE_CRAWL_CEILING;
    public static final int DISCOVERED_DOMAIN_LIMIT;
    public static final String SEARCH_STRING;

    static {
        DOMAIN_PAGE_CRAWL_CEILING = 10;
        SEARCH_STRING = "tomato";
        DISCOVERED_DOMAIN_LIMIT = 50;
    }

}
