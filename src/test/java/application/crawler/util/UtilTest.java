package application.crawler.util;

import org.junit.Before;
import org.junit.Test;
import service.messaging.MessengerImpl;

public class UtilTest {
    private MessengerImpl messenger;

    @Before
    public void setUp() throws Exception {
        messenger = new MessengerImpl();
    }

    @Test
    public void testCompression() throws Exception {
        String testStr = "SEFGINESAIFNASEIFONASEFWTSEFGINESAIFNASEIFONASEFWTSEFGINESAIFNASEIFONASEFWTSEFGINESAIFNASEIFONASEFF";
        String compressedStr = Util.compressString(testStr);
        String decompressedStr = Util.decompressString(compressedStr);
        assert(decompressedStr.equals(testStr));
    }

    @Test
    public void testCompressionThroughMessenger() throws Exception{
        String unCompressedStr = "SEFGINESAIFNASEIFONASEFWTSEFGINESAIFNASEIFONASEFWTSEFGINESAIFNASEIFONASEFWTSEFGINESAIFNASEIFONASEFF";
        String compressedStr = Util.compressString(unCompressedStr);

        messenger.getQueue("crawlResults").publishMessage(compressedStr);

        String msgRetrieved = messenger.getQueue("crawlResults").getMessage();
        String decompressedStr = Util.decompressString(msgRetrieved);

        assert(unCompressedStr.equals(decompressedStr));
    }

}
