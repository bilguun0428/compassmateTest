package mn.compassmate.protocol;

import mn.compassmate.protocol.CradlepointProtocol;
import mn.compassmate.protocol.CradlepointProtocolDecoder;
import org.junit.Test;
import mn.compassmate.ProtocolTest;

public class CradlepointProtocolDecoderTest extends ProtocolTest {

    @Test
    public void testDecode() throws Exception {

        CradlepointProtocolDecoder decoder = new CradlepointProtocolDecoder(new CradlepointProtocol());

        verifyPosition(decoder, text(
                "+12084014675,162658,4337.174385,N,11612.338373,W,0.0,,Verizon,,-71,-44,-11,,"));

    }

}
