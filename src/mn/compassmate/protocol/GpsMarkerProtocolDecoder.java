package mn.compassmate.protocol;

import org.jboss.netty.channel.Channel;
import mn.compassmate.BaseProtocolDecoder;
import mn.compassmate.DeviceSession;
import mn.compassmate.helper.Parser;
import mn.compassmate.helper.PatternBuilder;
import mn.compassmate.model.Position;

import java.net.SocketAddress;
import java.util.regex.Pattern;

public class GpsMarkerProtocolDecoder extends BaseProtocolDecoder {

    public GpsMarkerProtocolDecoder(GpsMarkerProtocol protocol) {
        super(protocol);
    }

    private static final Pattern PATTERN = new PatternBuilder()
            .text("$GM")
            .number("d")                         // type
            .number("(?:xx)?")                   // index
            .number("(d{15})")                   // imei
            .number("T(dd)(dd)(dd)")             // date (ddmmyy)
            .number("(dd)(dd)(dd)?")             // time (hhmmss)
            .expression("([NS])")
            .number("(dd)(dd)(dddd)")            // latitude
            .expression("([EW])")
            .number("(ddd)(dd)(dddd)")           // longitude
            .number("(ddd)")                     // speed
            .number("(ddd)")                     // course
            .number("(x)")                       // satellites
            .number("(dd)")                      // battery
            .number("(d)")                       // input
            .number("(d)")                       // output
            .number("(ddd)")                     // temperature
            .any()
            .compile();

    @Override
    protected Object decode(
            Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {

        Parser parser = new Parser(PATTERN, (String) msg);
        if (!parser.matches()) {
            return null;
        }

        Position position = new Position();
        position.setProtocol(getProtocolName());

        DeviceSession deviceSession = getDeviceSession(channel, remoteAddress, parser.next());
        if (deviceSession == null) {
            return null;
        }
        position.setDeviceId(deviceSession.getDeviceId());

        position.setTime(parser.nextDateTime(Parser.DateTimeFormat.DMY_HMS));

        position.setValid(true);
        position.setLatitude(parser.nextCoordinate(Parser.CoordinateFormat.HEM_DEG_MIN_MIN));
        position.setLongitude(parser.nextCoordinate(Parser.CoordinateFormat.HEM_DEG_MIN_MIN));
        position.setSpeed(parser.nextDouble(0));
        position.setCourse(parser.nextDouble(0));

        position.set(Position.KEY_SATELLITES, parser.nextHexInt(0));
        position.set(Position.KEY_BATTERY_LEVEL, parser.nextInt(0));
        position.set(Position.KEY_INPUT, parser.next());
        position.set(Position.KEY_OUTPUT, parser.next());
        position.set(Position.PREFIX_TEMP + 1, parser.next());

        return position;
    }

}
