package mn.compassmate.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import mn.compassmate.BaseProtocolDecoder;
import mn.compassmate.DeviceSession;
import mn.compassmate.helper.Checksum;
import mn.compassmate.helper.Parser;
import mn.compassmate.helper.PatternBuilder;
import mn.compassmate.model.CellTower;
import mn.compassmate.model.Network;
import mn.compassmate.model.Position;

import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class CityeasyProtocolDecoder extends BaseProtocolDecoder {

    public CityeasyProtocolDecoder(CityeasyProtocol protocol) {
        super(protocol);
    }

    private static final Pattern PATTERN = new PatternBuilder()
            .groupBegin()
            .number("(dddd)(dd)(dd)")            // date (yyyymmdd)
            .number("(dd)(dd)(dd),")             // time (hhmmss)
            .number("([AV]),")                   // validity
            .number("(d+),")                     // satellites
            .number("([NS]),(d+.d+),")           // latitude
            .number("([EW]),(d+.d+),")           // longitude
            .number("(d+.d),")                   // speed
            .number("(d+.d),")                   // hdop
            .number("(d+.d)")                    // altitude
            .groupEnd("?").text(";")
            .number("(d+),")                     // mcc
            .number("(d+),")                     // mnc
            .number("(d+),")                     // lac
            .number("(d+)")                      // cell
            .any()
            .compile();

    public static final int MSG_ADDRESS_REQUEST = 0x0001;
    public static final int MSG_STATUS = 0x0002;
    public static final int MSG_LOCATION_REPORT = 0x0003;
    public static final int MSG_LOCATION_REQUEST = 0x0004;
    public static final int MSG_LOCATION_INTERVAL = 0x0005;
    public static final int MSG_PHONE_NUMBER = 0x0006;
    public static final int MSG_MONITORING = 0x0007;
    public static final int MSG_TIMEZONE = 0x0008;

    @Override
    protected Object decode(
            Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {

        ChannelBuffer buf = (ChannelBuffer) msg;

        buf.skipBytes(2); // header
        buf.readUnsignedShort(); // length

        String imei = ChannelBuffers.hexDump(buf.readBytes(7));
        DeviceSession deviceSession = getDeviceSession(
                channel, remoteAddress, imei, imei + Checksum.luhn(Long.parseLong(imei)));
        if (deviceSession == null) {
            return null;
        }

        int type = buf.readUnsignedShort();

        if (type == MSG_LOCATION_REPORT || type == MSG_LOCATION_REQUEST) {

            String sentence = buf.toString(buf.readerIndex(), buf.readableBytes() - 8, StandardCharsets.US_ASCII);
            Parser parser = new Parser(PATTERN, sentence);
            if (!parser.matches()) {
                return null;
            }

            Position position = new Position();
            position.setProtocol(getProtocolName());
            position.setDeviceId(deviceSession.getDeviceId());

            if (parser.hasNext(15)) {

                position.setTime(parser.nextDateTime());

                position.setValid(parser.next().equals("A"));
                position.set(Position.KEY_SATELLITES, parser.nextInt());

                position.setLatitude(parser.nextCoordinate(Parser.CoordinateFormat.HEM_DEG));
                position.setLongitude(parser.nextCoordinate(Parser.CoordinateFormat.HEM_DEG));

                position.setSpeed(parser.nextDouble(0));
                position.set(Position.KEY_HDOP, parser.nextDouble(0));
                position.setAltitude(parser.nextDouble(0));

            } else {

                getLastLocation(position, null);

            }

            position.setNetwork(new Network(CellTower.from(
                    parser.nextInt(0), parser.nextInt(0), parser.nextInt(0), parser.nextInt(0))));

            return position;
        }

        return null;
    }

}