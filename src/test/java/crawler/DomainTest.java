package crawler;

import application.crawler.Url;
import application.crawler.domain.Domain;
import org.junit.Before;


public class DomainTest {
    private Domain domain;

    @Before
    public void setUp() throws Exception {
        domain =  new Domain(new Url("http://jgrapht.org/"));
        domain.run();
    }

}
