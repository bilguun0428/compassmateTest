package mn.compassmate.protocol;

import org.jboss.netty.channel.Channel;
import mn.compassmate.BaseProtocolDecoder;
import mn.compassmate.DeviceSession;
import mn.compassmate.helper.DateBuilder;
import mn.compassmate.helper.Parser;
import mn.compassmate.helper.PatternBuilder;
import mn.compassmate.model.Position;

import java.net.SocketAddress;
import java.util.regex.Pattern;

public class XexunProtocolDecoder extends BaseProtocolDecoder {

    private final boolean full;

    public XexunProtocolDecoder(XexunProtocol protocol, boolean full) {
        super(protocol);
        this.full = full;
    }

    private static final Pattern PATTERN_BASIC = new PatternBuilder()
            .expression("G[PN]RMC,")
            .number("(?:(dd)(dd)(dd))?.?d*,")    // time (hhmmss)
            .expression("([AV]),")               // validity
            .number("(d*?)(d?d.d+),([NS]),")     // latitude
            .number("(d*?)(d?d.d+),([EW])?,")    // longitude
            .number("(d+.?d*),")                 // speed
            .number("(d+.?d*)?,")                // course
            .number("(?:(dd)(dd)(dd))?,")        // date (ddmmyy)
            .expression("[^*]*").text("*")
            .number("xx")                        // checksum
            .expression("\\r\\n").optional()
            .expression(",([FL]),")              // signal
            .expression("([^,]*),").optional()   // alarm
            .any()
            .number("imei:(d+),")                // imei
            .compile();

    private static final Pattern PATTERN_FULL = new PatternBuilder()
            .any()
            .number("(d+),")                     // serial
            .expression("([^,]+)?,")             // phone number
            .expression(PATTERN_BASIC.pattern())
            .number("(d+),")                     // satellites
            .number("(-?d+.d+)?,")               // altitude
            .number("[FL]:(d+.d+)V")             // power
            .any()
            .compile();

    private String decodeStatus(Position position, String value) {
        if (value != null) {
            switch (value.toLowerCase()) {
                case "acc on":
                    position.set(Position.KEY_IGNITION, true);
                    break;
                case "acc off":
                    position.set(Position.KEY_IGNITION, false);
                    break;
                case "help me!":
                    position.set(Position.KEY_ALARM, Position.ALARM_SOS);
                    break;
                case "low battery":
                    position.set(Position.KEY_ALARM, Position.ALARM_LOW_BATTERY);
                    break;
                case "move!":
                case "moved!":
                    position.set(Position.KEY_ALARM, Position.ALARM_MOVEMENT);
                    break;
                default:
                    break;
            }
        }
        return null;
    }

    @Override
    protected Object decode(
            Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {

        Pattern pattern = PATTERN_BASIC;
        if (full) {
            pattern = PATTERN_FULL;
        }

        Parser parser = new Parser(pattern, (String) msg);
        if (!parser.matches()) {
            return null;
        }

        Position position = new Position();
        position.setProtocol(getProtocolName());

        if (full) {
            position.set("serial", parser.next());
            position.set("number", parser.next());
        }

        DateBuilder dateBuilder = new DateBuilder()
                .setTime(parser.nextInt(0), parser.nextInt(0), parser.nextInt(0));

        position.setValid(parser.next().equals("A"));
        position.setLatitude(parser.nextCoordinate());
        position.setLongitude(parser.nextCoordinate());

        position.setSpeed(convertSpeed(parser.nextDouble(0), "kn"));

        position.setCourse(parser.nextDouble(0));

        dateBuilder.setDateReverse(parser.nextInt(0), parser.nextInt(0), parser.nextInt(0));
        position.setTime(dateBuilder.getDate());

        position.set("signal", parser.next());

        decodeStatus(position, parser.next());

        DeviceSession deviceSession = getDeviceSession(channel, remoteAddress, parser.next());
        if (deviceSession == null) {
            return null;
        }
        position.setDeviceId(deviceSession.getDeviceId());

        if (full) {
            position.set(Position.KEY_SATELLITES, parser.nextInt());

            position.setAltitude(parser.nextDouble(0));

            position.set(Position.KEY_POWER, parser.nextDouble(0));
        }

        return position;
    }

}
