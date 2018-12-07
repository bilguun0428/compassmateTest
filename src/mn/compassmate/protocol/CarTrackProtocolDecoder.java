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

public class CarTrackProtocolDecoder extends BaseProtocolDecoder {

    public CarTrackProtocolDecoder(CarTrackProtocol protocol) {
        super(protocol);
    }

    private static final Pattern PATTERN = new PatternBuilder()
            .text("$$")                          // header
            .number("(d+)")                      // device id
            .text("?").expression("*")
            .text("&A")
            .number("(dddd)")                    // command
            .text("&B")
            .number("(dd)(dd)(dd).(ddd),")       // time (hhmmss.sss)
            .expression("([AV]),")               // validity
            .number("(dd)(dd.dddd),")            // latitude
            .expression("([NS]),")
            .number("(ddd)(dd.dddd),")           // longitude
            .expression("([EW]),")
            .number("(d+.d*)?,")                 // speed
            .number("(d+.d*)?,")                 // course
            .number("(dd)(dd)(dd)")              // date (ddmmyy)
            .any()
            .expression("&C([^&]*)")             // io
            .expression("&D([^&]*)")             // odometer
            .expression("&E([^&]*)")             // alarm
            .expression("&Y([^&]*)").optional()  // adc
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

        DateBuilder dateBuilder = new DateBuilder()
                .setTime(parser.nextInt(0), parser.nextInt(0), parser.nextInt(0), parser.nextInt(0));

        position.setValid(parser.next().equals("A"));
        position.setLatitude(parser.nextCoordinate());
        position.setLongitude(parser.nextCoordinate());
        position.setSpeed(parser.nextDouble(0));
        position.setCourse(parser.nextDouble(0));

        dateBuilder.setDateReverse(parser.nextInt(0), parser.nextInt(0), parser.nextInt(0));
        position.setTime(dateBuilder.getDate());

        position.set(Position.PREFIX_IO + 1, parser.next());

        String odometer = parser.next();
        odometer = odometer.replace(":", "A");
        odometer = odometer.replace(";", "B");
        odometer = odometer.replace("<", "C");
        odometer = odometer.replace("=", "D");
        odometer = odometer.replace(">", "E");
        odometer = odometer.replace("?", "F");
        position.set(Position.KEY_ODOMETER, Integer.parseInt(odometer, 16));

        parser.next(); // there is no meaningful alarms
        position.set(Position.PREFIX_ADC + 1, parser.next());

        return position;
    }

}
