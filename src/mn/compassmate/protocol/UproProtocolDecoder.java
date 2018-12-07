package mn.compassmate.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import mn.compassmate.BaseProtocolDecoder;
import mn.compassmate.DeviceSession;
import mn.compassmate.helper.BitUtil;
import mn.compassmate.helper.DateBuilder;
import mn.compassmate.helper.Parser;
import mn.compassmate.helper.PatternBuilder;
import mn.compassmate.model.CellTower;
import mn.compassmate.model.Network;
import mn.compassmate.model.Position;

import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.regex.Pattern;

public class UproProtocolDecoder extends BaseProtocolDecoder {

    public UproProtocolDecoder(UproProtocol protocol) {
        super(protocol);
    }

    private static final Pattern PATTERN_HEADER = new PatternBuilder()
            .text("*")
            .expression("..20")
            .expression("([01])")                // ack
            .number("(d+),")                     // device id
            .expression("(.)")                   // type
            .expression("(.)")                   // subtype
            .any()
            .compile();

    private static final Pattern PATTERN_LOCATION = new PatternBuilder()
            .number("(dd)(dd)(dd)")              // time (hhmmss)
            .number("(dd)(dd)(dddd)")            // latitude
            .number("(ddd)(dd)(dddd)")           // longitude
            .number("(d)")                       // flags
            .number("(dd)")                      // speed
            .number("(dd)")                      // course
            .number("(dd)(dd)(dd)")              // date (ddmmyy)
            .compile();

    private void decodeLocation(Position position, String data) {
        Parser parser = new Parser(PATTERN_LOCATION, data);
        if (parser.matches()) {

            DateBuilder dateBuilder = new DateBuilder()
                    .setTime(parser.nextInt(0), parser.nextInt(0), parser.nextInt(0));

            position.setValid(true);
            position.setLatitude(parser.nextCoordinate(Parser.CoordinateFormat.DEG_MIN_MIN));
            position.setLongitude(parser.nextCoordinate(Parser.CoordinateFormat.DEG_MIN_MIN));

            int flags = parser.nextInt(0);
            position.setValid(BitUtil.check(flags, 0));
            if (!BitUtil.check(flags, 1)) {
                position.setLatitude(-position.getLatitude());
            }
            if (!BitUtil.check(flags, 2)) {
                position.setLongitude(-position.getLongitude());
            }

            position.setSpeed(parser.nextInt(0) * 2);
            position.setCourse(parser.nextInt(0) * 10);

            dateBuilder.setDateReverse(parser.nextInt(0), parser.nextInt(0), parser.nextInt(0));
            position.setTime(dateBuilder.getDate());

        }
    }

    @Override
    protected Object decode(
            Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {

        ChannelBuffer buf = (ChannelBuffer) msg;

        if (buf.getByte(buf.readerIndex()) != '*') {
            throw new ParseException(null, 0);
        }

        int headerIndex = buf.indexOf(buf.readerIndex(), buf.writerIndex(), (byte) '&');
        if (headerIndex < 0) {
            headerIndex = buf.writerIndex();
        }
        String header = buf.readBytes(headerIndex - buf.readerIndex()).toString(StandardCharsets.US_ASCII);

        Parser parser = new Parser(PATTERN_HEADER, header);
        if (!parser.matches()) {
            return null;
        }

        boolean reply = parser.next().equals("1");

        DeviceSession deviceSession = getDeviceSession(channel, remoteAddress, parser.next());
        if (deviceSession == null) {
            return null;
        }

        Position position = new Position();
        position.setProtocol(getProtocolName());
        position.setDeviceId(deviceSession.getDeviceId());

        String type = parser.next();
        String subtype = parser.next();

        if (reply && channel != null) {
            channel.write("*MG20Y" + type + subtype + "#");
        }

        while (buf.readable()) {

            buf.readByte(); // skip delimiter

            byte dataType = buf.readByte();

            int delimiterIndex = buf.indexOf(buf.readerIndex(), buf.writerIndex(), (byte) '&');
            if (delimiterIndex < 0) {
                delimiterIndex = buf.writerIndex();
            }

            ChannelBuffer data = buf.readBytes(delimiterIndex - buf.readerIndex());

            switch (dataType) {
                case 'A':
                    decodeLocation(position, data.toString(StandardCharsets.US_ASCII));
                    break;
                case 'B':
                    position.set(Position.KEY_STATUS, data.toString(StandardCharsets.US_ASCII));
                    break;
                case 'C':
                    long odometer = 0;
                    while (data.readable()) {
                        odometer <<= 4;
                        odometer += data.readByte() - (byte) '0';
                    }
                    position.set(Position.KEY_ODOMETER, odometer * 2 * 1852 / 3600);
                    break;
                case 'P':
                    position.setNetwork(new Network(CellTower.from(
                            Integer.parseInt(data.readBytes(4).toString(StandardCharsets.US_ASCII)),
                            Integer.parseInt(data.readBytes(4).toString(StandardCharsets.US_ASCII)),
                            Integer.parseInt(data.readBytes(4).toString(StandardCharsets.US_ASCII), 16),
                            Integer.parseInt(data.readBytes(4).toString(StandardCharsets.US_ASCII), 16))));
                    break;
                case 'Q':
                    position.set("obd-pid", ChannelBuffers.hexDump(data));
                    break;
                case 'R':
                    position.set("odb-travel", ChannelBuffers.hexDump(data));
                    break;
                case 'S':
                    position.set("obd-traffic", ChannelBuffers.hexDump(data));
                    break;
                default:
                    break;
            }

        }

        if (position.getLatitude() != 0 && position.getLongitude() != 0) {
            return position;
        }

        return null;
    }

}
