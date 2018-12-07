package mn.compassmate.protocol;

import mn.compassmate.protocol.AlematicsProtocol;
import mn.compassmate.protocol.AlematicsProtocolDecoder;
import org.junit.Test;
import mn.compassmate.ProtocolTest;

public class AlematicsProtocolDecoderTest extends ProtocolTest {

    @Test
    public void testDecode() throws Exception {

        AlematicsProtocolDecoder decoder = new AlematicsProtocolDecoder(new AlematicsProtocol());

        verifyPosition(decoder, text(
                "$T,2,552,868259020159698,20170515060949,20170515060949,25.035277,121.561986,0,202,78,1.0,8,1,0,0.000,12.768,1629,38,12770,4109,9"));

        verifyPosition(decoder, text(
                "$T,2,553,868259020159698,20170515061019,20170515061019,25.035295,121.561981,0,202,79,1.0,8,1,0,0.000,12.768,1629,38,12772,4109,8"));

        verifyPosition(decoder, text(
                "$T,4,4,868259020159698,20170515061033,20170515061033,25.035303,121.561975,0,202,81,1.7,6,1,0,0.000,12.770,1629,0,$S,A1,1,,12345.67,88.4,301.5,,2593.25,12.4,89.2,,5999.44,789.572,2345.67,,10763,1024,5,"));

        verifyPosition(decoder, text(
                "$T,2,554,868259020159698,20170515061049,20170515061049,25.035309,121.561976,0,202,82,1.1,7,1,0,0.000,12.768,1629,38,12770,4109,9"));

        verifyPosition(decoder, text(
                "$T,4,5,868259020159698,20170515061058,20170515061058,25.035308,121.561976,0,202,82,1.2,7,1,0,0.000,12.772,1629,0,$S,A1,1,,12345.67,88.4,301.5,,2593.25,12.4,89.2,,5999.44,789.572,2345.67,,10763,1024,5,"));

        verifyPosition(decoder, text(
                "$T,50,592,868259020159698,20170515062915,20170515062915,25.035005,121.561555,0,31,89,3.7,5,1,0,0.000,12.752,1629,38,12752,4203,6"));

        verifyPosition(decoder, text(
                "$T,50,594,868259020159698,20170515062928,20170515062928,25.035151,121.561671,0,31,93,1.8,5,0,0,0.000,12.752,1629,38,12756,4205,6"));

    }

}
