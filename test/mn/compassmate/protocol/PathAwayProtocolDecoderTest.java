package mn.compassmate.protocol;

import mn.compassmate.protocol.PathAwayProtocol;
import mn.compassmate.protocol.PathAwayProtocolDecoder;
import org.junit.Test;
import mn.compassmate.ProtocolTest;

public class PathAwayProtocolDecoderTest extends ProtocolTest {

    @Test
    public void testDecode() throws Exception {

        PathAwayProtocolDecoder decoder = new PathAwayProtocolDecoder(new PathAwayProtocol());
        
        verifyPosition(decoder, request(
                "?UserName=name&Password=pass&LOC=$PWS,1,\"Roger\",,,100107,122846,45.317270,-79.642219,45.00,42,1,\"Comment\",0*58"));

    }

}
