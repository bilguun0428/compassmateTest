package mn.compassmate.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import mn.compassmate.BaseProtocolDecoder;
import mn.compassmate.DeviceSession;
import mn.compassmate.helper.Parser;
import mn.compassmate.helper.PatternBuilder;
import mn.compassmate.helper.UnitsConverter;
import mn.compassmate.model.Position;

import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.regex.Pattern;

public class WondexProtocolDecoder extends BaseProtocolDecoder {

    public WondexProtocolDecoder(WondexProtocol protocol) {
        super(protocol);
    }

    private static final Pattern PATTERN = new PatternBuilder()
            .number("[^d]*")                     // header
            .number("(d+),")                     // device identifier
            .number("(dddd)(dd)(dd)")            // date (yyyymmdd)
            .number("(dd)(dd)(dd),")             // time (hhmmss)
            .number("(-?d+.d+),")                // longitude
            .number("(-?d+.d+),")                // latitude
            .number("(d+),")                     // speed
            .number("(d+),")                     // course
            .number("(-?d+.?d*),")               // altitude
            .number("(d+),")                     // satellites
            .number("(d+),?")                    // event
            .number("(d+.d+)V,").optional()      // battery
            .number("(d+.d+)?,?")                // odometer
            .number("(d+)?,?")                   // input
            .number("(d+.d+)?,?")                // adc1
            .number("(d+.d+)?,?")                // adc2
            .number("(d+)?")                     // output
            .any()
            .compile();

    @Override
    protected Object decode(
            Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {

        ChannelBuffer buf = (ChannelBuffer) msg;

        if (buf.getUnsignedByte(0) == 0xD0) {

            long deviceId = ((Long.reverseBytes(buf.getLong(0))) >> 32) & 0xFFFFFFFFL;
            getDeviceSession(channel, remoteAddress, String.valueOf(deviceId));

            return null;
        } else if (buf.toString(StandardCharsets.US_ASCII).startsWith("$OK:")
                || buf.toString(StandardCharsets.US_ASCII).startsWith("$ERR:")
                  || buf.toString(StandardCharsets.US_ASCII).startsWith("$MSG:")) {

            DeviceSession deviceSession = getDeviceSession(channel, remoteAddress);

            Position position = new Position();
            position.setProtocol(getProtocolName());
            position.setDeviceId(deviceSession.getDeviceId());
            getLastLocation(position, new Date());
            position.set(Position.KEY_RESULT, buf.toString(StandardCharsets.US_ASCII));

            return position;
        } else {

            Parser parser = new Parser(PATTERN, buf.toString(StandardCharsets.US_ASCII));
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

            position.setTime(parser.nextDateTime());

            position.setLongitude(parser.nextDouble(0));
            position.setLatitude(parser.nextDouble(0));
            position.setSpeed(UnitsConverter.knotsFromKph(parser.nextDouble(0)));
            position.setCourse(parser.nextDouble(0));
            position.setAltitude(parser.nextDouble(0));

            int satellites = parser.nextInt(0);
            position.setValid(satellites != 0);
            position.set(Position.KEY_SATELLITES, satellites);

            position.set(Position.KEY_EVENT, parser.next());
            position.set(Position.KEY_BATTERY, parser.nextDouble());
            if (parser.hasNext()) {
                position.set(Position.KEY_ODOMETER, parser.nextDouble(0) * 1000);
            }
            position.set(Position.KEY_INPUT, parser.next());
            position.set(Position.PREFIX_ADC + 1, parser.next());
            position.set(Position.PREFIX_ADC + 2, parser.next());
            position.set(Position.KEY_OUTPUT, parser.next());

            return position;
        }

    }

}
