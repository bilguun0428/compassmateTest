package mn.compassmate.protocol;

import mn.compassmate.protocol.M2mProtocol;
import mn.compassmate.protocol.M2mProtocolDecoder;
import org.junit.Test;
import mn.compassmate.ProtocolTest;

public class M2mProtocolDecoderTest extends ProtocolTest {

    @Test
    public void testDecode() throws Exception {

        M2mProtocolDecoder decoder = new M2mProtocolDecoder(new M2mProtocol());

        verifyNull(decoder, binary(
                "235A3C2A2624215C287D70212A21254C7C6421220B0B0B"));

        verifyPosition(decoder, binary(
                "A6E12C2AAADA4628326B2059576E30202A2FE85D20200B"));

    }

}
