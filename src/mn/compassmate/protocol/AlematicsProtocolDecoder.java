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

public class AlematicsProtocolDecoder extends BaseProtocolDecoder {

    public AlematicsProtocolDecoder(AlematicsProtocol protocol) {
        super(protocol);
    }

    private static final Pattern PATTERN = new PatternBuilder()
            .text("$T,")
            .number("(d+),")                     // type
            .number("(d+),")                     // index
            .number("(d+),")                     // id
            .number("(dddd)(dd)(dd)")            // gps date
            .number("(dd)(dd)(dd),")             // gps time
            .number("(dddd)(dd)(dd)")            // device date
            .number("(dd)(dd)(dd),")             // device time
            .number("(-?d+.d+),")                // latitude
            .number("(-?d+.d+),")                // longitude
            .number("(d+),")                     // speed
            .number("(d+),")                     // course
            .number("(-?d+),")                   // altitude
            .number("(d+.d),")                   // hdop
            .number("(d+),")                     // satellites
            .number("(d+),")                     // input
            .number("(d+),")                     // output
            .number("(d+.d+),")                  // adc
            .number("(d+.d+),")                  // power
            .number("(d+),")                     // odometer
            .groupBegin()
            .text("0,$S,")
            .expression("(.*)")                  // text message
            .or()
            .number("(d+),")                     // extra mask
            .expression("(.*)")                  // extra data
            .groupEnd()
            .compile();

    private void decodeExtras(Position position, Parser parser) {

        int mask = parser.nextInt();
        String[] data = parser.next().split(",");

        int index = 0;

        if (BitUtil.check(mask, 0)) {
            index++; // pulse counter 3
        }

        if (BitUtil.check(mask, 1)) {
            position.set(Position.KEY_POWER, Integer.parseInt(data[index++]));
        }

        if (BitUtil.check(mask, 2)) {
            position.set(Position.KEY_BATTERY, Integer.parseInt(data[index++]));
        }

        if (BitUtil.check(mask, 3)) {
            position.set(Position.KEY_OBD_SPEED, Integer.parseInt(data[index++]));
        }

        if (BitUtil.check(mask, 4)) {
            position.set(Position.KEY_RPM, Integer.parseInt(data[index++]));
        }

        if (BitUtil.check(mask, 5)) {
            position.set(Position.KEY_RSSI, Integer.parseInt(data[index++]));
        }

        if (BitUtil.check(mask, 6)) {
            index++; // pulse counter 2
        }

        if (BitUtil.check(mask, 7)) {
            index++; // magnetic rotation sensor rpm
        }

    }

    @Override
    protected Object decode(
            Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {

        Parser parser = new Parser(PATTERN, (String) msg);
        if (!parser.matches()) {
            return null;
        }

        Position position = new Position();
        position.setProtocol(getProtocolName());

        position.set(Position.KEY_TYPE, parser.nextInt());
        position.set(Position.KEY_INDEX, parser.nextInt());

        DeviceSession deviceSession = getDeviceSession(channel, remoteAddress, parser.next());
        if (deviceSession == null) {
            return null;
        }
        position.setDeviceId(deviceSession.getDeviceId());

        position.setFixTime(parser.nextDateTime());
        position.setDeviceTime(parser.nextDateTime());

        position.setValid(true);
        position.setLatitude(parser.nextDouble(0));
        position.setLongitude(parser.nextDouble(0));
        position.setSpeed(UnitsConverter.knotsFromKph(parser.nextInt(0)));
        position.setCourse(parser.nextInt(0));
        position.setAltitude(parser.nextInt(0));

        position.set(Position.KEY_HDOP, parser.nextDouble());
        position.set(Position.KEY_SATELLITES, parser.nextInt());
        position.set(Position.KEY_INPUT, parser.nextInt());
        position.set(Position.KEY_OUTPUT, parser.nextInt());
        position.set(Position.PREFIX_ADC + 1, parser.nextDouble());
        position.set(Position.KEY_POWER, parser.nextDouble());
        position.set(Position.KEY_ODOMETER, parser.nextInt());

        if (parser.hasNext()) {
            position.set("text", parser.next());
        } else {
            decodeExtras(position, parser);
        }

        return position;
    }

}
