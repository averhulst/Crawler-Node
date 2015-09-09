package crawler;

import application.crawler.domain.RobotsTxt;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class RobotsTxtTest {
    private RobotsTxt robotsTxt;
    private String robotsTxtSource = "";
    private List<URI> siteMapControls = new ArrayList<URI>();

    @Before
    public void setUp() throws Exception {
        siteMapControls.add(new URI("http://www.test.com/sitemap"));
        siteMapControls.add(new URI("http://www.test.com/sitemap2"));
        siteMapControls.add(new URI("http://www.test.com/sitemap3"));
        try {

            String line;
            BufferedReader br = new BufferedReader(new FileReader("src/test/resources/robots.txt"));

            while ((line = br.readLine()) != null) {
                robotsTxtSource += line + "\n";
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        robotsTxt = new RobotsTxt(robotsTxtSource);
    }

    @After
    public void tearDown() throws Exception {}

    @Test
    public void testGetCrawlDelay() throws Exception {
        assert(robotsTxt.getCrawlDelay() ==  2000);
    }

    @Test
    public void testUrlIsAllowed() throws Exception {
        //tests /*
        assert(!robotsTxt.urlIsAllowed(new URI("http://www.test.com/allUAdisallowed/testAllClause/testAllClause.html")));
        assert(!robotsTxt.urlIsAllowed(new URI("http://www.test.com/allUAdisallowed/testAllClause/")));

        //tests explicitly allowed path, where the subpatch(s) match a catch-all disallow directive
        assert( robotsTxt.urlIsAllowed(new URI("http://www.test.com/allUAdisallowed/testAllClause/explicitlyAllowedPath.html")));
        //tests URLS whose paths do not fall under either a disallow or allow directive, should be implicitly allowed
        assert( robotsTxt.urlIsAllowed(new URI("http://www.test.com/whatever/iShouldBeAllowed.html")));
        assert( robotsTxt.urlIsAllowed(new URI("http://www.test.com/allowed/iShouldBeAllowed.html")));

        //disallowed paths applicable to other crawlers, not us, should be allowed
        assert( robotsTxt.urlIsAllowed(new URI("http://www.test.com/testdisallowedUA1/disallowed/disallowed1.html")));
        assert( robotsTxt.urlIsAllowed(new URI("http://www.test.com/testdisallowedUA2/disallowed/disallowed2.html")));
    }

    @Test
    public void testSiteMapDirective(){
        assert(robotsTxt.hasSiteMap());
        assert(robotsTxt.getSiteMapUrls().equals(siteMapControls));
    }
}
