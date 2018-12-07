package mn.compassmate.protocol;

import org.jboss.netty.channel.Channel;
import mn.compassmate.BaseProtocolDecoder;
import mn.compassmate.DeviceSession;
import mn.compassmate.helper.Parser;
import mn.compassmate.helper.PatternBuilder;
import mn.compassmate.helper.UnitsConverter;
import mn.compassmate.model.CellTower;
import mn.compassmate.model.Network;
import mn.compassmate.model.Position;

import java.net.SocketAddress;
import java.util.regex.Pattern;

public class FlexCommProtocolDecoder extends BaseProtocolDecoder {

    public FlexCommProtocolDecoder(FlexCommProtocol protocol) {
        super(protocol);
    }

    private static final Pattern PATTERN = new PatternBuilder()
            .text("7E")
            .number("(dd)")                      // status
            .number("(d{15})")                   // imei
            .number("(dddd)(dd)(dd)")            // date (yyyymmdd)
            .number("(dd)(dd)(dd)")              // time (hhmmss)
            .expression("([01])")                // valid
            .number("(d{9})")                    // latitude
            .number("(d{10})")                   // longitude
            .number("(d{4})")                    // altitude
            .number("(ddd)")                     // speed
            .number("(ddd)")                     // course
            .number("(dd)")                      // satellites view
            .number("(dd)")                      // satellites used
            .number("(dd)")                      // rssi
            .number("(ddd)")                     // mcc
            .number("(ddd)")                     // mnc
            .number("(x{6})")                    // lac
            .number("(x{6})")                    // cid
            .expression("([01])([01])([01])")    // input
            .expression("([01])([01])")          // output
            .number("(ddd)")                     // fuel
            .number("(d{4})")                    // temperature
            .number("(ddd)")                     // battery
            .number("(ddd)")                     // power
            .any()
            .compile();

    private static double parseSignedValue(Parser parser, int decimalPoints) {
        String stringValue = parser.next();
        boolean negative = stringValue.charAt(0) == '1';
        double value = Integer.parseInt(stringValue.substring(1)) * Math.pow(10, -decimalPoints);
        return negative ? -value : value;
    }

    @Override
    protected Object decode(
            Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {

        Parser parser = new Parser(PATTERN, (String) msg);
        if (!parser.matches()) {
            return null;
        }

        Position position = new Position();
        position.setProtocol(getProtocolName());

        position.set(Position.KEY_STATUS, parser.nextInt());

        DeviceSession deviceSession = getDeviceSession(channel, remoteAddress, parser.next());
        if (deviceSession == null) {
            return null;
        }
        position.setDeviceId(deviceSession.getDeviceId());

        position.setTime(parser.nextDateTime());
        position.setValid(parser.next().equals("1"));
        position.setLatitude(parseSignedValue(parser, 6));
        position.setLongitude(parseSignedValue(parser, 6));
        position.setAltitude(parseSignedValue(parser, 0));
        position.setSpeed(UnitsConverter.knotsFromKph(parser.nextInt()));
        position.setCourse(parser.nextDouble(0));

        position.set(Position.KEY_SATELLITES_VISIBLE, parser.nextInt());
        position.set(Position.KEY_SATELLITES, parser.nextInt());
        position.set(Position.KEY_RSSI, parser.nextInt());

        position.setNetwork(new Network(CellTower.from(
                parser.nextInt(), parser.nextInt(), parser.nextHexInt(), parser.nextHexInt())));

        for (int i = 1; i <= 3; i++) {
            position.set(Position.PREFIX_IN + i, parser.nextInt());
        }

        for (int i = 1; i <= 2; i++) {
            position.set(Position.PREFIX_OUT + i, parser.nextInt());
        }

        position.set(Position.KEY_FUEL_LEVEL, parser.nextInt());
        position.set(Position.PREFIX_TEMP + 1, parseSignedValue(parser, 0));
        position.set(Position.KEY_BATTERY_LEVEL, parser.nextInt());
        position.set(Position.KEY_POWER, parser.nextInt() * 0.1);

        if (channel != null) {
            channel.write("{01}");
        }

        return position;
    }

}