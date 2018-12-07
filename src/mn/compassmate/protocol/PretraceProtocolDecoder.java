package mn.compassmate.protocol;

import org.jboss.netty.channel.Channel;
import mn.compassmate.BaseProtocolDecoder;
import mn.compassmate.DeviceSession;
import mn.compassmate.helper.Parser;
import mn.compassmate.helper.PatternBuilder;
import mn.compassmate.helper.UnitsConverter;
import mn.compassmate.model.Position;

import java.net.SocketAddress;
import java.util.regex.Pattern;

public class PretraceProtocolDecoder extends BaseProtocolDecoder {

    public PretraceProtocolDecoder(PretraceProtocol protocol) {
        super(protocol);
    }

    private static final Pattern PATTERN = new PatternBuilder()
            .text("(")
            .number("(d{15})")                   // imei
            .number("Uddd")                      // type
            .number("d")                         // gps type
            .expression("([AV])")                // validity
            .number("(dd)(dd)(dd)")              // date (yymmdd)
            .number("(dd)(dd)(dd)")              // time (hhmmss)
            .number("(dd)(dd.dddd)")             // latitude
            .expression("([NS])")
            .number("(ddd)(dd.dddd)")            // longitude
            .expression("([EW])")
            .number("(ddd)")                     // speed
            .number("(ddd)")                     // course
            .number("(xxx)")                     // altitude
            .number("(x{8})")                    // odometer
            .number("(x)")                       // satellites
            .number("(dd)")                      // hdop
            .number("(dd)")                      // gsm
            .expression("(.{8})")                // state
            .any()
            .text("^")
            .number("xx")                        // checksum
            .compile();

    @Override
    protected Object decode(
            Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {

        Parser parser = new Parser(PATTERN, (String) msg);
        if (!parser.matches()) {
            return null;
        }

        DeviceSession deviceSession = getDeviceSession(channel, remoteAddress, parser.next());
        if (deviceSession == null) {
            return null;
        }

        Position position = new Position();
        position.setProtocol(getProtocolName());
        position.setDeviceId(deviceSession.getDeviceId());

        position.setValid(parser.next().equals("A"));

        position.setTime(parser.nextDateTime());

        position.setLatitude(parser.nextCoordinate());
        position.setLongitude(parser.nextCoordinate());
        position.setSpeed(UnitsConverter.knotsFromKph(parser.nextInt(0)));
        position.setCourse(parser.nextInt(0));
        position.setAltitude(parser.nextHexInt(0));

        position.set(Position.KEY_ODOMETER, parser.nextHexInt(0));
        position.set(Position.KEY_SATELLITES, parser.nextHexInt(0));
        position.set(Position.KEY_HDOP, parser.nextInt(0));
        position.set(Position.KEY_RSSI, parser.nextInt(0));

        return position;
    }

}
