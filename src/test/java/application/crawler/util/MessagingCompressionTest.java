package application.crawler.util;

import org.junit.Before;
import org.junit.Test;
import service.messaging.MessengerImpl;

import java.util.HashMap;
import java.util.Map;

public class MessagingCompressionTest {
    private MessengerImpl messenger;
    private String testStr;
    @Before
    public void setUp() throws Exception {
        messenger = new MessengerImpl();
        testStr = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean pulvinar rutrum augue. Sed tincidunt ullamcorper lorem sit amet aliquam. Praesent auctor ligula sem, ut euismod lectus tincidunt et. Nunc convallis, purus sit amet gravida semper, dolor lorem ornare turpis, a egestas turpis urna vitae odio. Praesent eu erat eget dui efficitur pharetra id nec nunc. Etiam accumsan urna nisl, tincidunt maximus enim finibus et. Aenean non ex quis purus pretium commodo ac vitae libero. Aliquam nec viverra massa, sit amet tincidunt ligula. Duis tristique, nisl quis tincidunt luctus, sapien augue tincidunt sem, vel condimentum massa dui sit amet orci. Ut efficitur nibh in dictum fringilla. Quisque condimentum elementum ipsum sit amet porttitor.";
    }

    @Test
    public void testCompression() throws Exception {
        String compressedStr = Util.compressString(testStr);
        String decompressedStr = Util.decompressString(compressedStr);

        assert(decompressedStr.equals(testStr));
    }

    @Test
    public void testCompressionThroughMessenger() throws Exception{
        //Ensure queues have been purged before testing
        String localCompressedStr = Util.compressString(testStr);
        messenger.getQueue("crawlResults").publishMessage(localCompressedStr);
        messenger.getQueue("crawlResults").addHeaders(
            new HashMap(){{
                put("Content-Encoding", "gzip");
            }}
        );

        String msgRetrieved = messenger.getQueue("crawlResults").getMessage();

        System.out.println(msgRetrieved);
        System.out.println("WTF");
        String decompressedStr = Util.decompressString(msgRetrieved);

        System.out.println(decompressedStr);
        decompressedStr = Util.decompressString(msgRetrieved);

        assert(decompressedStr.equals(testStr));
    }

}
