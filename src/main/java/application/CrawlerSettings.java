package application;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationFactory;
import org.apache.commons.configuration.DefaultConfigurationBuilder;

public class CrawlerSettings {
    private static final int DOMAIN_PAGE_CRAWL_CEILING;

    static {
        DOMAIN_PAGE_CRAWL_CEILING = 5;
    }

}
