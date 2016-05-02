package application.crawler.util;

import application.crawler.SiteMapParser;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class SiteMapParserTest {
    private SiteMapParser siteMapParser;
    private List<java.net.URI> urlListControl = new ArrayList<URI>();

    @Before
    public void setUp() throws Exception {
        String source = "";
        try {

            String line;
            BufferedReader br = new BufferedReader(new FileReader("src/test/resources/sitemap.xml"));

            while ((line = br.readLine()) != null) {
                source += line + "\n";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        siteMapParser = new SiteMapParser(new URI("http://www.sitemappro.com/"), source);

        try {

            String line;
            BufferedReader br = new BufferedReader(new FileReader("src/test/resources/sitemapcontrol.txt"));

            while ((line = br.readLine()) != null) {
                urlListControl.add(new URI(line));
                source += line + "\n";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testXmlParser() throws MalformedURLException {
        assert(siteMapParser.getURIs().size() == urlListControl.size());

        for(URI urlControl : urlListControl) {
            siteMapParser.getURIs().contains(urlListControl);
        }
    }
}
