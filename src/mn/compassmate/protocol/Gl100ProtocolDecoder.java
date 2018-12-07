package mn.compassmate.protocol;

import org.jboss.netty.channel.Channel;
import mn.compassmate.BaseProtocolDecoder;
import mn.compassmate.DeviceSession;
import mn.compassmate.helper.Parser;
import mn.compassmate.helper.PatternBuilder;
import mn.compassmate.model.Position;

import java.net.SocketAddress;
import java.util.regex.Pattern;

public class Gl100ProtocolDecoder extends BaseProtocolDecoder {

    public Gl100ProtocolDecoder(Gl100Protocol protocol) {
        super(protocol);
    }

    private static final Pattern PATTERN = new PatternBuilder()
            .text("+RESP:")
            .expression("GT...,")
            .number("(d{15}),")                  // imei
            .groupBegin()
            .number("d+,")                       // number
            .number("d,")                        // reserved / geofence id
            .number("d+")                        // reserved / geofence alert // battery
            .or()
            .number("[^,]*")                     // calling number
            .groupEnd(",")
            .expression("([01]),")               // gps fix
            .number("(d+.d),")                   // speed
            .number("(d+),")                     // course
            .number("(-?d+.d),")                 // altitude
            .number("d*,")                       // gps accuracy
            .number("(-?d+.d+),")                // longitude
            .number("(-?d+.d+),")                // latitude
            .number("(dddd)(dd)(dd)")            // date (yyyymmdd)
            .number("(dd)(dd)(dd),")             // time (hhmmss)
            .any()
            .compile();

    @Override
    protected Object decode(
            Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {

        String sentence = (String) msg;

        if (sentence.contains("AT+GTHBD=") && channel != null) {
            String response = "+RESP:GTHBD,GPRS ACTIVE,";
            response += sentence.substring(9, sentence.lastIndexOf(','));
            response += '\0';
            channel.write(response); // heartbeat response
        }

        Parser parser = new Parser(PATTERN, sentence);
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

        position.setValid(parser.nextInt(0) == 0);
        position.setSpeed(parser.nextDouble(0));
        position.setCourse(parser.nextDouble(0));
        position.setAltitude(parser.nextDouble(0));
        position.setLongitude(parser.nextDouble(0));
        position.setLatitude(parser.nextDouble(0));

        position.setTime(parser.nextDateTime());

        return position;
    }

}
