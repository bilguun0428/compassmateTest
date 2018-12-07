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

public class EskyProtocolDecoder extends BaseProtocolDecoder {

    public EskyProtocolDecoder(EskyProtocol protocol) {
        super(protocol);
    }

    private static final Pattern PATTERN = new PatternBuilder()
            .text("EO;")                         // header
            .number("d+;")                       // index
            .number("(d+);")                     // imei
            .text("R;")                          // data type
            .number("(d+)").text("+")            // satellites
            .number("(dd)(dd)(dd)")              // date
            .number("(dd)(dd)(dd)").text("+")    // time
            .number("(-?d+.d+)").text("+")       // latitude
            .number("(-?d+.d+)").text("+")       // longitude
            .number("(d+.d+)").text("+")         // speed
            .number("(d+)").text("+")            // course
            .text("0x").number("(d+)").text("+") // input
            .number("(d+)").text("+")            // message type
            .number("(d+)").text("+")            // odometer
            .number("(d+)")                      // voltage
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

        position.set(Position.KEY_SATELLITES, parser.nextInt());

        position.setValid(true);
        position.setTime(parser.nextDateTime());
        position.setLatitude(parser.nextDouble());
        position.setLongitude(parser.nextDouble());
        position.setSpeed(UnitsConverter.knotsFromMps(parser.nextDouble()));
        position.setCourse(parser.nextDouble());

        position.set(Position.KEY_INPUT, parser.nextHexInt());
        position.set(Position.KEY_EVENT, parser.nextInt());
        position.set(Position.KEY_ODOMETER, parser.nextInt());
        position.set(Position.KEY_POWER, parser.nextInt());

        return position;
    }

}
