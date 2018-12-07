package mn.compassmate.protocol;

import org.jboss.netty.channel.Channel;
import mn.compassmate.BaseProtocolDecoder;
import mn.compassmate.DeviceSession;
import mn.compassmate.helper.Checksum;
import mn.compassmate.helper.DateBuilder;
import mn.compassmate.helper.Parser;
import mn.compassmate.helper.PatternBuilder;
import mn.compassmate.model.Position;

import java.net.SocketAddress;
import java.util.regex.Pattern;

public class GpsGateProtocolDecoder extends BaseProtocolDecoder {

    public GpsGateProtocolDecoder(GpsGateProtocol protocol) {
        super(protocol);
    }

    private static final Pattern PATTERN_GPRMC = new PatternBuilder()
            .text("$GPRMC,")
            .number("(dd)(dd)(dd).?d*,")         // time (hhmmss)
            .expression("([AV]),")               // validity
            .number("(dd)(dd.d+),")              // latitude
            .expression("([NS]),")
            .number("(ddd)(dd.d+),")             // longitude
            .expression("([EW]),")
            .number("(d+.d+)?,")                 // speed
            .number("(d+.d+)?,")                 // course
            .number("(dd)(dd)(dd)")              // date (ddmmyy)
            .any()
            .compile();

    private static final Pattern PATTERN_FRCMD = new PatternBuilder()
            .text("$FRCMD,")
            .number("(d+),")                     // imei
            .expression("[^,]*,")                // command
            .expression("[^,]*,")
            .number("(d+)(dd.d+),")              // latitude
            .expression("([NS]),")
            .number("(d+)(dd.d+),")              // longitude
            .expression("([EW]),")
            .number("(d+.?d*),")                 // altitude
            .number("(d+.?d*),")                 // speed
            .number("(d+.?d*)?,")                // course
            .number("(dd)(dd)(dd),")             // date (ddmmyy)
            .number("(dd)(dd)(dd).?d*,")         // time (hhmmss)
            .expression("([01])")                // validity
            .any()
            .compile();

    private void send(Channel channel, String message) {
        if (channel != null) {
            channel.write(message + Checksum.nmea(message) + "\r\n");
        }
    }

    @Override
    protected Object decode(
            Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {

        String sentence = (String) msg;

        if (sentence.startsWith("$FRLIN,")) {

            // Login
            int beginIndex = sentence.indexOf(',', 7);
            if (beginIndex != -1) {
                beginIndex += 1;
                int endIndex = sentence.indexOf(',', beginIndex);
                if (endIndex != -1) {
                    String imei = sentence.substring(beginIndex, endIndex);
                    DeviceSession deviceSession = getDeviceSession(channel, remoteAddress, imei);
                    if (deviceSession != null) {
                        if (channel != null) {
                            send(channel, "$FRSES," + channel.getId());
                        }
                    } else {
                        send(channel, "$FRERR,AuthError,Unknown device");
                    }
                } else {
                    send(channel, "$FRERR,AuthError,Parse error");
                }
            } else {
                send(channel, "$FRERR,AuthError,Parse error");
            }

        } else if (sentence.startsWith("$FRVER,")) {

            // Version check
            send(channel, "$FRVER,1,0,GpsGate Server 1.0");

        } else if (sentence.startsWith("$GPRMC,")) {

            DeviceSession deviceSession = getDeviceSession(channel, remoteAddress);
            if (deviceSession == null) {
                return null;
            }

            Parser parser = new Parser(PATTERN_GPRMC, sentence);
            if (!parser.matches()) {
                return null;
            }

            Position position = new Position();
            position.setProtocol(getProtocolName());
            position.setDeviceId(deviceSession.getDeviceId());

            DateBuilder dateBuilder = new DateBuilder()
                    .setTime(parser.nextInt(0), parser.nextInt(0), parser.nextInt(0));

            position.setValid(parser.next().equals("A"));
            position.setLatitude(parser.nextCoordinate());
            position.setLongitude(parser.nextCoordinate());
            position.setSpeed(parser.nextDouble(0));
            position.setCourse(parser.nextDouble(0));

            dateBuilder.setDateReverse(parser.nextInt(0), parser.nextInt(0), parser.nextInt(0));
            position.setTime(dateBuilder.getDate());

            return position;

        } else if (sentence.startsWith("$FRCMD,")) {

            Parser parser = new Parser(PATTERN_FRCMD, sentence);
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

            position.setLatitude(parser.nextCoordinate());
            position.setLongitude(parser.nextCoordinate());
            position.setAltitude(parser.nextDouble(0));
            position.setSpeed(parser.nextDouble(0));
            position.setCourse(parser.nextDouble(0));

            position.setTime(parser.nextDateTime(Parser.DateTimeFormat.DMY_HMS));

            position.setValid(parser.next().equals("1"));

            return position;

        }

        return null;
    }

}
