package mn.compassmate.protocol;

import org.jboss.netty.channel.Channel;
import mn.compassmate.BaseProtocolDecoder;
import mn.compassmate.DeviceSession;
import mn.compassmate.helper.Parser;
import mn.compassmate.helper.PatternBuilder;
import mn.compassmate.model.Position;

import java.net.SocketAddress;
import java.util.regex.Pattern;

public class Tr900ProtocolDecoder extends BaseProtocolDecoder {

    public Tr900ProtocolDecoder(Tr900Protocol protocol) {
        super(protocol);
    }

    private static final Pattern PATTERN = new PatternBuilder()
            .number(">(d+),")                    // id
            .number("d+,")                       // period
            .number("(d),")                      // fix
            .number("(dd)(dd)(dd),")             // date (yymmdd)
            .number("(dd)(dd)(dd),")             // time (hhmmss)
            .expression("([EW])")
            .number("(ddd)(dd.d+),")             // longitude
            .expression("([NS])")
            .number("(dd)(dd.d+),")              // latitude
            .expression("[^,]*,")                // command
            .number("(d+.?d*),")                 // speed
            .number("(d+.?d*),")                 // course
            .number("(d+),")                     // gsm
            .number("(d+),")                     // event
            .number("(d+)-")                     // adc
            .number("(d+),")                     // battery
            .number("d+,")                       // impulses
            .number("(d+),")                     // input
            .number("(d+)")                      // status
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

        position.setValid(parser.nextInt(0) == 1);

        position.setTime(parser.nextDateTime());

        position.setLongitude(parser.nextCoordinate(Parser.CoordinateFormat.HEM_DEG_MIN));
        position.setLatitude(parser.nextCoordinate(Parser.CoordinateFormat.HEM_DEG_MIN));
        position.setSpeed(parser.nextDouble(0));
        position.setCourse(parser.nextDouble(0));

        position.set(Position.KEY_RSSI, parser.nextDouble());
        position.set(Position.KEY_EVENT, parser.nextInt(0));
        position.set(Position.PREFIX_ADC + 1, parser.nextInt(0));
        position.set(Position.KEY_BATTERY, parser.nextInt(0));
        position.set(Position.KEY_INPUT, parser.next());
        position.set(Position.KEY_STATUS, parser.next());

        return position;
    }

}
