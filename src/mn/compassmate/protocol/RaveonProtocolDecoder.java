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

public class RaveonProtocolDecoder extends BaseProtocolDecoder {

    public RaveonProtocolDecoder(RaveonProtocol protocol) {
        super(protocol);
    }

    private static final Pattern PATTERN = new PatternBuilder()
            .text("$PRAVE,")
            .number("(d+),")                     // id
            .number("d+,")
            .number("(-?)(d+)(dd.d+),")          // latitude
            .number("(-?)(d+)(dd.d+),")          // longitude
            .number("(dd)(dd)(dd),")             // time (hhmmss)
            .number("(d),")                      // validity
            .number("(d+),")                     // satellites
            .number("(-?d+),")                   // altitude
            .number("(-?d+),")                   // temperature
            .number("(d+.d+),")                  // power
            .number("(d+),")                     // inputs
            .number("(-?d+),")                   // gsm
            .number("(d+),")                     // speed
            .number("(d+),")                     // course
            .expression("([PMACIVSX])?,")        // status
            .any()
            .compile();

    @Override
    protected Object decode(
            Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {

        String sentence = (String) msg;

        Parser parser = new Parser(PATTERN, sentence);
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

        position.setLatitude(parser.nextCoordinate(Parser.CoordinateFormat.HEM_DEG_MIN));
        position.setLongitude(parser.nextCoordinate(Parser.CoordinateFormat.HEM_DEG_MIN));

        position.setTime(parser.nextDateTime(Parser.DateTimeFormat.HMS));

        position.setValid(parser.nextInt(0) != 0);

        position.set(Position.KEY_SATELLITES, parser.nextInt(0));

        position.setAltitude(parser.nextInt(0));

        position.set(Position.PREFIX_TEMP + 1, parser.nextInt(0));
        position.set(Position.KEY_POWER, parser.nextDouble(0));
        position.set(Position.KEY_INPUT, parser.nextInt(0));
        position.set(Position.KEY_RSSI, parser.nextInt(0));

        position.setSpeed(UnitsConverter.knotsFromKph(parser.nextInt(0)));
        position.setCourse(parser.nextInt(0));

        position.set(Position.KEY_ALARM, parser.next());

        return position;
    }

}
