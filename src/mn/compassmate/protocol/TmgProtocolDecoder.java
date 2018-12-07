package mn.compassmate.protocol;

import org.jboss.netty.channel.Channel;
import mn.compassmate.BaseProtocolDecoder;
import mn.compassmate.DeviceSession;
import mn.compassmate.helper.BitUtil;
import mn.compassmate.helper.Parser;
import mn.compassmate.helper.PatternBuilder;
import mn.compassmate.helper.UnitsConverter;
import mn.compassmate.model.Position;

import java.net.SocketAddress;
import java.util.regex.Pattern;

public class TmgProtocolDecoder extends BaseProtocolDecoder {

    public TmgProtocolDecoder(TmgProtocol protocol) {
        super(protocol);
    }

    private static final Pattern PATTERN = new PatternBuilder()
            .text("$")
            .expression("(...),")                // type
            .expression("[LH],")                 // history
            .number("(d+),")                     // imei
            .number("(dd)(dd)(dddd),")           // date (ddmmyyyy)
            .number("(dd)(dd)(dd),")             // time (hhmmss)
            .number("(d),")                      // status
            .number("(dd)(dd.d+),")              // latitude
            .expression("([NS]),")
            .number("(ddd)(dd.d+),")             // longitude
            .expression("([EW]),")
            .number("(d+.?d*),")                 // speed
            .number("(d+.?d*),")                 // course
            .number("(-?d+.?d*),")               // altitude
            .number("(d+.d+),")                  // hdop
            .number("(d+),")                     // satellites
            .number("(d+),")                     // visible satellites
            .number("([^,]*),")                  // operator
            .number("(d+),")                     // rssi
            .number("[^,]*,")                    // cid
            .expression("([01]),")               // ignition
            .number("(d+.?d*),")                 // battery
            .number("(d+.?d*),")                 // power
            .expression("([01]+),")              // input
            .expression("([01]+),")              // output
            .expression("[01]+,")                // temper status
            .number("(d+.?d*)[^,]*,")            // adc1
            .number("(d+.?d*)[^,]*,")            // adc2
            .number("d+.?d*,")                   // trip meter
            .expression("([^,]*),")              // software version
            .expression("([^,]*),").optional()   // rfid
            .any()
            .compile();

    @Override
    protected Object decode(
            Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {

        Parser parser = new Parser(PATTERN, (String) msg);
        if (!parser.matches()) {
            return null;
        }

        String type = parser.next();

        DeviceSession deviceSession = getDeviceSession(channel, remoteAddress, parser.next());
        if (deviceSession == null) {
            return null;
        }

        Position position = new Position();
        position.setProtocol(getProtocolName());
        position.setDeviceId(deviceSession.getDeviceId());

        switch (type) {
            case "rmv":
                position.set(Position.KEY_ALARM, Position.ALARM_POWER_CUT);
                break;
            case "ebl":
                position.set(Position.KEY_ALARM, Position.ALARM_LOW_POWER);
                break;
            case "ibl":
                position.set(Position.KEY_ALARM, Position.ALARM_LOW_BATTERY);
                break;
            case "tmp":
            case "smt":
            case "btt":
                position.set(Position.KEY_ALARM, Position.ALARM_TAMPERING);
                break;
            case "ion":
                position.set(Position.KEY_IGNITION, true);
                break;
            case "iof":
                position.set(Position.KEY_IGNITION, false);
                break;
            default:
                break;
        }

        position.setTime(parser.nextDateTime(Parser.DateTimeFormat.DMY_HMS));

        position.setValid(parser.nextInt(0) > 0);
        position.setLatitude(parser.nextCoordinate());
        position.setLongitude(parser.nextCoordinate());
        position.setSpeed(UnitsConverter.knotsFromKph(parser.nextDouble(0)));
        position.setCourse(parser.nextDouble(0));
        position.setAltitude(parser.nextDouble(0));

        position.set(Position.KEY_HDOP, parser.nextDouble(0));
        position.set(Position.KEY_SATELLITES, parser.nextInt(0));
        position.set(Position.KEY_SATELLITES_VISIBLE, parser.nextInt(0));
        position.set(Position.KEY_OPERATOR, parser.next());
        position.set(Position.KEY_RSSI, parser.nextInt(0));
        position.set(Position.KEY_IGNITION, parser.nextInt(0) == 1);
        position.set(Position.KEY_BATTERY, parser.nextDouble(0));
        position.set(Position.KEY_POWER, parser.nextDouble(0));

        int input = parser.nextBinInt(0);
        int output = parser.nextBinInt(0);

        if (!BitUtil.check(input, 0)) {
            position.set(Position.KEY_ALARM, Position.ALARM_SOS);
        }

        position.set(Position.KEY_INPUT, input);
        position.set(Position.KEY_OUTPUT, output);

        position.set(Position.PREFIX_ADC + 1, parser.nextDouble(0));
        position.set(Position.PREFIX_ADC + 2, parser.nextDouble(0));
        position.set(Position.KEY_VERSION_FW, parser.next());
        position.set(Position.KEY_DRIVER_UNIQUE_ID, parser.next());

        return position;
    }

}
