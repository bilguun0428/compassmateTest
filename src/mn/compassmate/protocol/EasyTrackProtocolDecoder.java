package mn.compassmate.protocol;

import org.jboss.netty.channel.Channel;
import mn.compassmate.BaseProtocolDecoder;
import mn.compassmate.DeviceSession;
import mn.compassmate.helper.BitUtil;
import mn.compassmate.helper.DateBuilder;
import mn.compassmate.helper.Parser;
import mn.compassmate.helper.PatternBuilder;
import mn.compassmate.helper.UnitsConverter;
import mn.compassmate.model.Position;

import java.net.SocketAddress;
import java.util.regex.Pattern;

public class EasyTrackProtocolDecoder extends BaseProtocolDecoder {

    public EasyTrackProtocolDecoder(EasyTrackProtocol protocol) {
        super(protocol);
    }

    private static final Pattern PATTERN = new PatternBuilder()
            .text("*").expression("..,")         // manufacturer
            .number("(d+),")                     // imei
            .expression("([^,]{2}),")            // command
            .expression("([AV]),")               // validity
            .number("(xx)(xx)(xx),")             // date (yymmdd)
            .number("(xx)(xx)(xx),")             // time (hhmmss)
            .number("(x)(x{7}),")                // latitude
            .number("(x)(x{7}),")                // longitude
            .number("(x{4}),")                   // speed
            .number("(x{4}),")                   // course
            .number("(x{8}),")                   // status
            .number("(x+),")                     // signal
            .number("(d+),")                     // power
            .number("(x{4}),")                   // oil
            .number("(x+),?")                    // odometer
            .number("(d+)?")                     // altitude
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

        position.set(Position.KEY_COMMAND, parser.next());

        position.setValid(parser.next().equals("A"));

        DateBuilder dateBuilder = new DateBuilder()
                .setDate(parser.nextHexInt(0), parser.nextHexInt(0), parser.nextHexInt(0))
                .setTime(parser.nextHexInt(0), parser.nextHexInt(0), parser.nextHexInt(0));
        position.setTime(dateBuilder.getDate());

        if (BitUtil.check(parser.nextHexInt(0), 3)) {
            position.setLatitude(-parser.nextHexInt(0) / 600000.0);
        } else {
            position.setLatitude(parser.nextHexInt(0) / 600000.0);
        }

        if (BitUtil.check(parser.nextHexInt(0), 3)) {
            position.setLongitude(-parser.nextHexInt(0) / 600000.0);
        } else {
            position.setLongitude(parser.nextHexInt(0) / 600000.0);
        }

        position.setSpeed(UnitsConverter.knotsFromKph(parser.nextHexInt(0) / 100.0));
        position.setCourse(parser.nextHexInt(0) / 100.0);

        position.set(Position.KEY_STATUS, parser.next());
        position.set("signal", parser.next());
        position.set(Position.KEY_POWER, parser.nextDouble(0));
        position.set("oil", parser.nextHexInt(0));
        position.set(Position.KEY_ODOMETER, parser.nextHexInt(0) * 100);

        position.setAltitude(parser.nextDouble(0));

        return position;
    }

}
