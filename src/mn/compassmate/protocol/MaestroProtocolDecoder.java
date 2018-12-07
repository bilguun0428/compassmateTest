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

public class MaestroProtocolDecoder extends BaseProtocolDecoder {

    public MaestroProtocolDecoder(MaestroProtocol protocol) {
        super(protocol);
    }

    private static final Pattern PATTERN = new PatternBuilder()
            .text("@")
            .number("(d+),")                     // imei
            .number("d+,")                       // index
            .expression("[^,]+,")                // profile
            .expression("([01]),")               // validity
            .number("(d+.d+),")                  // battery
            .number("(d+),")                     // gsm
            .expression("([01]),")               // starter
            .expression("([01]),")               // ignition
            .number("(dd)/(dd)/(dd),")           // date (yy/mm/dd)
            .number("(dd):(dd):(dd),")           // time (hh:mm:ss)
            .number("(-?d+.d+),")                // longitude
            .number("(-?d+.d+),")                // latitude
            .number("(d+.?d*),")                 // altitude
            .number("(d+.?d*),")                 // speed
            .number("(d+.?d*),")                 // course
            .number("(d+),")                     // satellites
            .number("(d+.?d*),")                 // hdop
            .number("(d+.?d*)")                  // odometer
            .number(",(d+)").optional()          // adc
            .any()
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

        position.setValid(parser.nextInt(0) == 1);

        position.set(Position.KEY_BATTERY, parser.nextDouble(0));
        position.set(Position.KEY_RSSI, parser.nextInt(0));
        position.set(Position.KEY_CHARGE, parser.nextInt(0) == 1);
        position.set(Position.KEY_IGNITION, parser.nextInt(0) == 1);

        position.setTime(parser.nextDateTime());

        position.setLatitude(parser.nextDouble(0));
        position.setLongitude(parser.nextDouble(0));
        position.setAltitude(parser.nextDouble(0));
        position.setSpeed(UnitsConverter.knotsFromMph(parser.nextDouble(0)));
        position.setCourse(parser.nextDouble(0));

        position.set(Position.KEY_SATELLITES, parser.nextInt(0));
        position.set(Position.KEY_HDOP, parser.nextDouble(0));
        position.set(Position.KEY_ODOMETER, parser.nextDouble(0) * 1609.34);

        if (parser.hasNext()) {
            position.set(Position.PREFIX_ADC + 1, parser.nextInt(0));
        }

        return position;
    }

}
