package mn.compassmate.protocol;

import mn.compassmate.protocol.L100FrameDecoder;
import org.junit.Assert;
import org.junit.Test;
import mn.compassmate.ProtocolTest;

public class L100FrameDecoderTest extends ProtocolTest {

    @Test
    public void testDecode() throws Exception {

        L100FrameDecoder decoder = new L100FrameDecoder();

        Assert.assertEquals(
                binary("200141544c3335363839353033373533333734352c244750524d432c3131313731392e3030302c412c323833382e303034352c4e2c30373731332e333730372c452c302e30302c2c3132303831302c2c2c412a3735242c2330313130303131313030313031302c4e2e432c4e2e432c4e2e432c31323334352e36372c33312e342c342e322c32312c4d43432c4d4e432c4c41432c43656c6c494441544c027a"),
                decoder.decode(null, null, binary("200141544c3335363839353033373533333734352c244750524d432c3131313731392e3030302c412c323833382e303034352c4e2c30373731332e333730372c452c302e30302c2c3132303831302c2c2c412a3735242c2330313130303131313030313031302c4e2e432c4e2e432c4e2e432c31323334352e36372c33312e342c342e322c32312c4d43432c4d4e432c4c41432c43656c6c494441544c027a")));

        Assert.assertEquals(
                binary("200341544c3335363839353033373533333734352c244750524d432c3131313731392e3030302c412c323833382e303034352c4e2c30373731332e333730372c452c302e30302c2c3132303831302c2c2c412a3735244c4f432c436f6e6e61756768742043697263757320c2a0436f6e6e617567687420506c61636520c2a04e65772044656c686920c2a044656c6869c2a0496e6469612c2330313130303130313130313031302c322e332c33352e36372c38302c31323334352e36372c33312e342c342e322c32312c4d43432c4d4e432c4c41432c43656c6c494441544c047a"),
                decoder.decode(null, null, binary("200341544c3335363839353033373533333734352c244750524d432c3131313731392e3030302c412c323833382e303034352c4e2c30373731332e333730372c452c302e30302c2c3132303831302c2c2c412a3735244c4f432c436f6e6e61756768742043697263757320c2a0436f6e6e617567687420506c61636520c2a04e65772044656c686920c2a044656c6869c2a0496e6469612c2330313130303130313130313031302c322e332c33352e36372c38302c31323334352e36372c33312e342c342e322c32312c4d43432c4d4e432c4c41432c43656c6c494441544c047a")));

    }

}
