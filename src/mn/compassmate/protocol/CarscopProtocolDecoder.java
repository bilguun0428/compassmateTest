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

public class CarscopProtocolDecoder extends BaseProtocolDecoder {

    public CarscopProtocolDecoder(CarscopProtocol protocol) {
        super(protocol);
    }

    private static final Pattern PATTERN = new PatternBuilder()
            .text("*")
            .any()
            .number("(dd)(dd)(dd)")              // time (hhmmss)
            .expression("([AV])")                // validity
            .number("(dd)(dd.dddd)")             // latitude
            .expression("([NS])")
            .number("(ddd)(dd.dddd)")            // longitude
            .expression("([EW])")
            .number("(ddd.d)")                   // speed
            .number("(dd)(dd)(dd)")              // date (yymmdd)
            .number("(ddd.dd)")                  // course
            .groupBegin()
            .number("(d{8})")                    // state
            .number("L(d{6})")                   // odometer
            .groupEnd("?")
            .compile();

    @Override
    protected Object decode(
            Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {

        String sentence = (String) msg;

        DeviceSession deviceSession;
        int index = sentence.indexOf("UB05");
        if (index != -1) {
            String imei = sentence.substring(index + 4, index + 4 + 15);
            deviceSession = getDeviceSession(channel, remoteAddress, imei);
        } else {
            deviceSession = getDeviceSession(channel, remoteAddress);
        }
        if (deviceSession == null) {
            return null;
        }

        Parser parser = new Parser(PATTERN, sentence);
        if (!parser.matches()) {
            return null;
        }

        Position position = new Position();
        position.setDeviceId(deviceSession.getDeviceId());
        position.setProtocol(getProtocolName());

        DateBuilder dateBuilder = new DateBuilder()
                .setTime(parser.nextInt(0), parser.nextInt(0), parser.nextInt(0));

        position.setValid(parser.next().equals("A"));
        position.setLatitude(parser.nextCoordinate());
        position.setLongitude(parser.nextCoordinate());
        position.setSpeed(parser.nextDouble(0));

        dateBuilder.setDate(parser.nextInt(0), parser.nextInt(0), parser.nextInt(0));
        position.setTime(dateBuilder.getDate());

        position.setCourse(parser.nextDouble(0));

        if (parser.hasNext(2)) {
            position.set(Position.KEY_STATUS, parser.next());
            position.set(Position.KEY_ODOMETER, parser.nextInt(0));
        }

        return position;
    }

}
