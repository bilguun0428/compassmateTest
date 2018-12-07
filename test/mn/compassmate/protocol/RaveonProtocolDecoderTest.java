package mn.compassmate.protocol;

import mn.compassmate.protocol.RaveonProtocol;
import mn.compassmate.protocol.RaveonProtocolDecoder;
import org.junit.Test;
import mn.compassmate.ProtocolTest;

public class RaveonProtocolDecoderTest extends ProtocolTest {

    @Test
    public void testDecode() throws Exception {

        RaveonProtocolDecoder decoder = new RaveonProtocolDecoder(new RaveonProtocol());

        verifyPosition(decoder, text(
                "$PRAVE,0001,0001,3308.9051,-11713.1164,195348,1,10,168,31,13.3,3,-83,0,0,,1003.4*66"));

    }

}
