package mn.compassmate.protocol;

import mn.compassmate.protocol.FlexCommProtocolDecoder;
import mn.compassmate.protocol.FlexCommProtocol;
import org.junit.Test;
import mn.compassmate.ProtocolTest;

public class FlexCommProtocolDecoderTest extends ProtocolTest {

    @Test
    public void testDecode() throws Exception {

        FlexCommProtocolDecoder decoder = new FlexCommProtocolDecoder(new FlexCommProtocol());

        verifyPosition(decoder, text(
                "7E00865067022408382201705302358271024932258006712785200700022601010224100040002C5002A2210001000000010012342107"));

        verifyPosition(decoder, text(
                "7E27865067022408382201705241211301024932197006712794000910022481008234100040002C5002A2200011000000006306941827"));

    }

}
