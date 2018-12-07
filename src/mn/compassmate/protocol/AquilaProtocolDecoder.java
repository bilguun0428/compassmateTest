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

public class AquilaProtocolDecoder extends BaseProtocolDecoder {

    public AquilaProtocolDecoder(AquilaProtocol protocol) {
        super(protocol);
    }

    private static final Pattern PATTERN = new PatternBuilder()
            .text("$$")
            .expression("[^,]*,")                // client
            .number("(d+),")                     // device serial number
            .number("(d+),")                     // event
            .number("(-?d+.d+),")                // latitude
            .number("(-?d+.d+),")                // longitude
            .number("(dd)(dd)(dd)")              // date (yymmdd)
            .number("(dd)(dd)(dd),")             // time (hhmmss)
            .expression("([AV]),")               // validity
            .number("(d+),")                     // gsm
            .number("(d+),")                     // speed
            .number("(d+),")                     // distance
            .groupBegin()
            .number("d+,")                       // driver code
            .number("(d+),")                     // fuel
            .number("([01]),")                   // io 1
            .number("[01],")                     // case open switch
            .number("[01],")                     // over speed start
            .number("[01],")                     // over speed end
            .number("(?:d+,){3}")                // reserved
            .number("([01]),")                   // power status
            .number("([01]),")                   // io 2
            .number("d+,")                       // reserved
            .number("([01]),")                   // ignition
            .number("[01],")                     // ignition off event
            .number("(?:d+,){7}")                // reserved
            .number("[01],")                     // corner packet
            .number("(?:d+,){8}")                // reserved
            .number("([01]),")                   // course bit 0
            .number("([01]),")                   // course bit 1
            .number("([01]),")                   // course bit 2
            .number("([01]),")                   // course bit 3
            .or()
            .number("(d+),")                     // course
            .number("(?:d+,){3}")                // reserved
            .number("[01],")                     // over speed start
            .number("[01],")                     // over speed end
            .number("(?:d+,){3}")                // reserved
            .number("([01]),")                   // power status
            .number("(?:d+,){2}")                // reserved
            .number("[01],")                     // ignition on event
            .number("([01]),")                   // ignition
            .number("[01],")                     // ignition off event
            .number("(?:d+,){5}")                // reserved
            .number("[01],")                     // low battery
            .number("[01],")                     // corner packet
            .number("(?:d+,){6}")                // reserved
            .number("[01],")                     // hard acceleration
            .number("[01],")                     // hard breaking
            .number("[01],[01],[01],[01],")      // course bits
            .number("(d+),")                     // external voltage
            .number("(d+),")                     // internal voltage
            .number("(?:d+,){6}")                // reserved
            .expression("P([^,]+),")             // obd
            .expression("D([^,]+),")             // dtcs
            .number("-?d+,")                     // accelerometer x
            .number("-?d+,")                     // accelerometer y
            .number("-?d+,")                     // accelerometer z
            .number("d+,")                       // delta distance
            .or()
            .number("(d+),")                     // course
            .number("(d+),")                     // satellites
            .number("(d+.d+),")                  // hdop
            .number("(?:d+,){2}")                // reserved
            .number("(d+),")                     // adc 1
            .number("([01]),")                   // di 1
            .number("[01],")                     // case open
            .number("[01],")                     // over speed start
            .number("[01],")                     // over speed end
            .number("(?:[01],){2}")              // reserved
            .number("[01],")                     // immobilizer
            .number("([01]),")                   // power status
            .number("([01]),")                   // di 2
            .number("(?:[01],){2}")              // reserved
            .number("([01]),")                   // ignition
            .number("(?:[01],){6}")              // reserved
            .number("[01],")                     // low battery
            .number("[01],")                     // corner packet
            .number("(?:[01],){4}")              // reserved
            .number("[01],")                     // do 1
            .number("[01],")                     // reserved
            .number("[01],")                     // hard acceleration
            .number("[01],")                     // hard breaking
            .number("(?:[01],){4}")              // reserved
            .number("(d+),")                     // external voltage
            .number("(d+),")                     // internal voltage
            .groupEnd()
            .text("*")
            .number("xx")                        // checksum
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

        position.set(Position.KEY_EVENT, parser.nextInt(0));

        position.setLatitude(parser.nextDouble(0));
        position.setLongitude(parser.nextDouble(0));

        position.setTime(parser.nextDateTime());

        position.setValid(parser.next().equals("A"));

        position.set(Position.KEY_RSSI, parser.nextInt(0));

        position.setSpeed(UnitsConverter.knotsFromKph(parser.nextDouble(0)));

        position.set(Position.KEY_ODOMETER, parser.nextInt(0));

        if (parser.hasNext(9)) {

            position.set(Position.KEY_FUEL_LEVEL, parser.nextInt());
            position.set(Position.PREFIX_IN + 1, parser.next());
            position.set(Position.KEY_CHARGE, parser.next().equals("1"));
            position.set(Position.PREFIX_IN + 2, parser.next());

            position.set(Position.KEY_IGNITION, parser.nextInt(0) == 1);

            int course = (parser.nextInt(0) << 3) + (parser.nextInt(0) << 2)
                    + (parser.nextInt(0) << 1) + parser.nextInt(0);
            if (course > 0 && course <= 8) {
                position.setCourse((course - 1) * 45);
            }

        } else if (parser.hasNext(7)) {

            position.setCourse(parser.nextInt(0));

            position.set(Position.KEY_CHARGE, parser.next().equals("1"));
            position.set(Position.KEY_IGNITION, parser.nextInt(0) == 1);
            position.set(Position.KEY_POWER, parser.nextInt(0));
            position.set(Position.KEY_BATTERY, parser.nextInt(0));

            String obd = parser.next();
            position.set("obd", obd.substring(1, obd.length() - 1));

            String dtcs = parser.next();
            position.set(Position.KEY_DTCS, dtcs.substring(1, dtcs.length() - 1).replace('|', ' '));

        } else {

            position.setCourse(parser.nextInt(0));

            position.set(Position.KEY_SATELLITES, parser.nextInt(0));
            position.set(Position.KEY_HDOP, parser.nextDouble(0));
            position.set(Position.PREFIX_ADC + 1, parser.nextInt(0));
            position.set(Position.PREFIX_IN + 1, parser.nextInt(0));
            position.set(Position.KEY_CHARGE, parser.next().equals("1"));
            position.set(Position.PREFIX_IN + 2, parser.nextInt(0));
            position.set(Position.KEY_IGNITION, parser.nextInt(0) == 1);
            position.set(Position.KEY_POWER, parser.nextInt(0));
            position.set(Position.KEY_BATTERY, parser.nextInt(0));

        }

        return position;
    }

}