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

public class ArnaviProtocolDecoder extends BaseProtocolDecoder {

    public ArnaviProtocolDecoder(ArnaviProtocol protocol) {
        super(protocol);
    }

    private static final Pattern PATTERN = new PatternBuilder()
            .text("$AV,")
            .number("Vd,")                       // type
            .number("(d+),")                     // device id
            .number("(d+),")                     // index
            .number("(d+),")                     // power
            .number("(d+),")                     // battery
            .number("-?d+,")
            .expression("[01],")                 // movement
            .expression("([01]),")               // ignition
            .number("(d+),")                     // input
            .number("d+,d+,")                    // input 1
            .number("d+,d+,").optional()         // input 2
            .expression("[01],")                 // fix type
            .number("(d+),")                     // satellites
            .number("(dd)(dd)(dd),")             // time (hhmmss)
            .number("(dd)(dd.d+)([NS]),")        // latitude
            .number("(ddd)(dd.d+)([EW]),")       // longitude
            .number("(d+.d+),")                  // speed
            .number("(d+.d+),")                  // course
            .number("(dd)(dd)(dd)")              // date (ddmmyy)
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

        position.set(Position.KEY_INDEX, parser.nextInt(0));
        position.set(Position.KEY_POWER, parser.nextInt(0) * 0.01);
        position.set(Position.KEY_BATTERY, parser.nextInt(0) * 0.01);
        position.set(Position.KEY_IGNITION, parser.nextInt(0) == 1);
        position.set(Position.KEY_INPUT, parser.nextInt(0));
        position.set(Position.KEY_SATELLITES, parser.nextInt(0));

        DateBuilder dateBuilder = new DateBuilder()
                .setTime(parser.nextInt(0), parser.nextInt(0), parser.nextInt(0));

        position.setValid(true);
        position.setLatitude(parser.nextCoordinate());
        position.setLongitude(parser.nextCoordinate());
        position.setSpeed(parser.nextDouble(0));
        position.setCourse(parser.nextDouble(0));

        dateBuilder.setDateReverse(parser.nextInt(0), parser.nextInt(0), parser.nextInt(0));
        position.setTime(dateBuilder.getDate());

        return position;
    }

}
