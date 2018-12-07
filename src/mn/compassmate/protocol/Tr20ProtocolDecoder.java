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

public class Tr20ProtocolDecoder extends BaseProtocolDecoder {

    public Tr20ProtocolDecoder(Tr20Protocol protocol) {
        super(protocol);
    }

    private static final Pattern PATTERN_PING = new PatternBuilder()
            .text("%%")
            .expression("[^,]+,")
            .number("(d+)")
            .compile();

    private static final Pattern PATTERN_DATA = new PatternBuilder()
            .text("%%")
            .expression("([^,]+),")              // id
            .expression("([AL]),")               // validity
            .number("(dd)(dd)(dd)")              // date (yymmdd)
            .number("(dd)(dd)(dd),")             // time (hhmmss)
            .expression("([NS])")
            .number("(dd)(dd.d+)")               // latitude
            .expression("([EW])")
            .number("(ddd)(dd.d+),")             // longitude
            .number("(d+),")                     // speed
            .number("(d+),")                     // course
            .any()
            .compile();

    @Override
    protected Object decode(
            Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {

        Parser parser = new Parser(PATTERN_PING, (String) msg);
        if (parser.matches()) {
            if (channel != null) {
                channel.write("&&" + parser.next() + "\r\n"); // keep-alive response
            }
            return null;
        }

        parser = new Parser(PATTERN_DATA, (String) msg);
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

        position.setValid(parser.next().equals("A"));

        position.setTime(parser.nextDateTime());

        position.setLatitude(parser.nextCoordinate(Parser.CoordinateFormat.HEM_DEG_MIN));
        position.setLongitude(parser.nextCoordinate(Parser.CoordinateFormat.HEM_DEG_MIN));
        position.setSpeed(UnitsConverter.knotsFromKph(parser.nextDouble(0)));
        position.setCourse(parser.nextDouble(0));

        return position;
    }

}
