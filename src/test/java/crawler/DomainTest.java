package crawler;

import application.crawler.domain.Domain;
import org.junit.Before;

import java.net.URI;


public class DomainTest {
    private Domain domain;

    @Before
    public void setUp() throws Exception {
        domain =  new Domain(new URI("http://jgrapht.org/"));
        domain.run();
    }

}
