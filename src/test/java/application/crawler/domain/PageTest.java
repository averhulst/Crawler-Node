package application.crawler.domain;

import application.crawler.Request;
import application.crawler.Url;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class PageTest {
    private Page page;
    private String sourceCode;

    @Test
    public void testLinkParser() throws Exception {
        try {

            String line;
            BufferedReader br = new BufferedReader(new FileReader("src/test/resources/page.html"));

            while ((line = br.readLine()) != null) {
                sourceCode += line + "\n";
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        page = new Page(new Url("http://www.google.com"), sourceCode);

        assert(page.getDiscoveredPages().size() == 4);
        assert(page.getDiscoveredPages().contains(new Url("http://www.google.com")));
        assert(page.getDiscoveredPages().contains(new Url("http://www.google.com/")));
        assert(page.getDiscoveredPages().contains(new Url("http://www.google.com/test")));

        assert(page.getDiscoveredDomains().size() == 1);
        assert(page.getDiscoveredDomains().contains(new Url("http://www.external.com")));
    }

    @Test
    public void testGetDiscoveredDomains() throws Exception {

    }

    @Test
    public void testGetDiscoveredPages() throws Exception {

    }
}
