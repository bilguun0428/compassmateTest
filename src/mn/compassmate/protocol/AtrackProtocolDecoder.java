package mn.compassmate.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import mn.compassmate.BaseProtocolDecoder;
import mn.compassmate.Context;
import mn.compassmate.DeviceSession;
import mn.compassmate.helper.DateBuilder;
import mn.compassmate.helper.Parser;
import mn.compassmate.helper.PatternBuilder;
import mn.compassmate.helper.UnitsConverter;
import mn.compassmate.model.CellTower;
import mn.compassmate.model.Network;
import mn.compassmate.model.Position;

import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class AtrackProtocolDecoder extends BaseProtocolDecoder {

    private static final int MIN_DATA_LENGTH = 40;

    private boolean longDate;
    private boolean custom;
    private String form;

    private final Map<Integer, String> alarmMap = new HashMap<>();

    public AtrackProtocolDecoder(AtrackProtocol protocol) {
        super(protocol);

        longDate = Context.getConfig().getBoolean(getProtocolName() + ".longDate");

        custom = Context.getConfig().getBoolean(getProtocolName() + ".custom");
        form = Context.getConfig().getString(getProtocolName() + ".form");
        if (form != null) {
            custom = true;
        }

        for (String pair : Context.getConfig().getString(getProtocolName() + ".alarmMap", "").split(",")) {
            if (!pair.isEmpty()) {
                alarmMap.put(
                        Integer.parseInt(pair.substring(0, pair.indexOf('='))), pair.substring(pair.indexOf('=') + 1));
            }
        }
    }

    public void setLongDate(boolean longDate) {
        this.longDate = longDate;
    }

    public void setCustom(boolean custom) {
        this.custom = custom;
    }

    private static void sendResponse(Channel channel, SocketAddress remoteAddress, long rawId, int index) {
        if (channel != null) {
            ChannelBuffer response = ChannelBuffers.directBuffer(12);
            response.writeShort(0xfe02);
            response.writeLong(rawId);
            response.writeShort(index);
            channel.write(response, remoteAddress);
        }
    }

    private static String readString(ChannelBuffer buf) {
        String result = null;
        int index = buf.indexOf(buf.readerIndex(), buf.writerIndex(), (byte) 0);
        if (index > buf.readerIndex()) {
            result = buf.readBytes(index - buf.readerIndex()).toString(StandardCharsets.US_ASCII);
        }
        buf.readByte();
        return result;
    }

    private void readCustomData(Position position, ChannelBuffer buf, String form) {
        CellTower cellTower = new CellTower();
        String[] keys = form.substring(1).split("%");
        for (String key : keys) {
            switch (key) {
                case "SA":
                    position.set(Position.KEY_SATELLITES, buf.readUnsignedByte());
                    break;
                case "MV":
                    position.set(Position.KEY_POWER, buf.readUnsignedShort());
                    break;
                case "BV":
                    position.set(Position.KEY_BATTERY, buf.readUnsignedShort());
                    break;
                case "GQ":
                    cellTower.setSignalStrength((int) buf.readUnsignedByte());
                    break;
                case "CE":
                    cellTower.setCellId(buf.readUnsignedInt());
                    break;
                case "LC":
                    cellTower.setLocationAreaCode(buf.readUnsignedShort());
                    break;
                case "CN":
                    int combinedMobileCodes = (int) (buf.readUnsignedInt() % 100000); // cccnn
                    cellTower.setMobileCountryCode(combinedMobileCodes / 100);
                    cellTower.setMobileNetworkCode(combinedMobileCodes % 100);
                    break;
                case "RL":
                    buf.readUnsignedByte(); // rxlev
                    break;
                case "PC":
                    position.set(Position.PREFIX_COUNT + 1, buf.readUnsignedInt());
                    break;
                case "AT":
                    position.setAltitude(buf.readUnsignedInt());
                    break;
                case "RP":
                    position.set(Position.KEY_RPM, buf.readUnsignedShort());
                    break;
                case "GS":
                    position.set(Position.KEY_RSSI, buf.readUnsignedByte());
                    break;
                case "DT":
                    position.set(Position.KEY_ARCHIVE, buf.readUnsignedByte() == 1);
                    break;
                case "VN":
                    position.set(Position.KEY_VIN, readString(buf));
                    break;
                case "MF":
                    buf.readUnsignedShort(); // mass air flow rate
                    break;
                case "EL":
                    buf.readUnsignedByte(); // engine load
                    break;
                case "TR":
                    position.set(Position.KEY_THROTTLE, buf.readUnsignedByte());
                    break;
                case "ET":
                    position.set(Position.PREFIX_TEMP + 1, buf.readUnsignedShort());
                    break;
                case "FL":
                    position.set(Position.KEY_FUEL_LEVEL, buf.readUnsignedByte());
                    break;
                case "ML":
                    buf.readUnsignedByte(); // mil status
                    break;
                case "FC":
                    position.set(Position.KEY_FUEL_CONSUMPTION, buf.readUnsignedInt());
                    break;
                case "CI":
                    readString(buf); // format string
                    break;
                case "AV1":
                    position.set(Position.PREFIX_ADC + 1, buf.readUnsignedShort());
                    break;
                case "NC":
                    readString(buf); // gsm neighbor cell info
                    break;
                case "SM":
                    buf.readUnsignedShort(); // max speed between reports
                    break;
                case "GL":
                    readString(buf); // google link
                    break;
                case "MA":
                    readString(buf); // mac address
                    break;
                default:
                    break;
            }
        }

        if (cellTower.getMobileCountryCode() != null
            && cellTower.getMobileNetworkCode() != null
            && cellTower.getCellId() != null
            && cellTower.getLocationAreaCode() != null) {
            position.setNetwork(new Network(cellTower));
        } else if (cellTower.getSignalStrength() != null) {
            position.set(Position.KEY_RSSI, cellTower.getSignalStrength());
        }
    }

    private static final Pattern PATTERN_INFO = new PatternBuilder()
            .text("$INFO=")
            .number("(d+),")                     // unit id
            .expression("([^,]+),")              // model
            .expression("([^,]+),")              // firmware version
            .number("d+,")                       // imei
            .number("d+,")                       // imsi
            .number("d+,")                       // sim card id
            .number("(d+),")                     // power
            .number("(d+),")                     // battery
            .number("(d+),")                     // satellites
            .number("d+,")                       // gsm status
            .number("(d+),")                     // rssi
            .number("d+,")                       // connection status
            .number("d+")                        // antenna status
            .any()
            .compile();

    private Position decodeString(Channel channel, SocketAddress remoteAddress, String sentence) {
        Position position = new Position();
        position.setProtocol(getProtocolName());

        getLastLocation(position, null);

        DeviceSession deviceSession;

        if (sentence.startsWith("$INFO")) {

            Parser parser = new Parser(PATTERN_INFO, sentence);
            if (!parser.matches()) {
                return null;
            }

            deviceSession = getDeviceSession(channel, remoteAddress, parser.next());

            position.set("model", parser.next());
            position.set(Position.KEY_VERSION_FW, parser.next());
            position.set(Position.KEY_POWER, parser.nextInt() * 0.1);
            position.set(Position.KEY_BATTERY, parser.nextInt() * 0.1);
            position.set(Position.KEY_SATELLITES, parser.nextInt());
            position.set(Position.KEY_RSSI, parser.nextInt());

        } else {

            deviceSession = getDeviceSession(channel, remoteAddress);

            position.set(Position.KEY_RESULT, sentence);

        }

        if (deviceSession == null) {
            return null;
        } else {
            position.setDeviceId(deviceSession.getDeviceId());
            return position;
        }
    }

    @Override
    protected Object decode(
            Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {

        ChannelBuffer buf = (ChannelBuffer) msg;

        if (buf.getUnsignedShort(buf.readerIndex()) == 0xfe02) {
            if (channel != null) {
                channel.write(buf, remoteAddress); // keep-alive message
            }
            return null;
        } else if (buf.getByte(buf.readerIndex()) == '$') {
            return decodeString(channel, remoteAddress, buf.toString(StandardCharsets.US_ASCII).trim());
        }

        buf.skipBytes(2); // prefix
        buf.readUnsignedShort(); // checksum
        buf.readUnsignedShort(); // length
        int index = buf.readUnsignedShort();

        long id = buf.readLong();
        DeviceSession deviceSession = getDeviceSession(channel, remoteAddress, String.valueOf(id));
        if (deviceSession == null) {
            return null;
        }

        sendResponse(channel, remoteAddress, id, index);

        List<Position> positions = new LinkedList<>();

        while (buf.readableBytes() >= MIN_DATA_LENGTH) {

            Position position = new Position();
            position.setProtocol(getProtocolName());
            position.setDeviceId(deviceSession.getDeviceId());

            if (longDate) {

                DateBuilder dateBuilder = new DateBuilder()
                        .setDate(buf.readUnsignedShort(), buf.readUnsignedByte(), buf.readUnsignedByte())
                        .setTime(buf.readUnsignedByte(), buf.readUnsignedByte(), buf.readUnsignedByte());
                position.setTime(dateBuilder.getDate());

                buf.skipBytes(7 + 7);


            } else {

                position.setFixTime(new Date(buf.readUnsignedInt() * 1000));
                position.setDeviceTime(new Date(buf.readUnsignedInt() * 1000));
                buf.readUnsignedInt(); // send time
            }

            position.setValid(true);
            position.setLongitude(buf.readInt() * 0.000001);
            position.setLatitude(buf.readInt() * 0.000001);
            position.setCourse(buf.readUnsignedShort());

            int type = buf.readUnsignedByte();
            position.set(Position.KEY_TYPE, type);
            position.set(Position.KEY_ALARM, alarmMap.get(type));

            position.set(Position.KEY_ODOMETER, buf.readUnsignedInt() * 100);
            position.set(Position.KEY_HDOP, buf.readUnsignedShort() * 0.1);
            position.set(Position.KEY_INPUT, buf.readUnsignedByte());

            position.setSpeed(UnitsConverter.knotsFromKph(buf.readUnsignedShort()));

            position.set(Position.KEY_OUTPUT, buf.readUnsignedByte());
            position.set(Position.PREFIX_ADC + 1, buf.readUnsignedShort() * 0.001);

            position.set(Position.KEY_DRIVER_UNIQUE_ID, readString(buf));

            position.set(Position.PREFIX_TEMP + 1, buf.readShort() * 0.1);
            position.set(Position.PREFIX_TEMP + 2, buf.readShort() * 0.1);

            position.set("message", readString(buf));

            if (custom) {
                String form = this.form;
                if (form == null) {
                    form = readString(buf).substring("%CI".length());
                }
                readCustomData(position, buf, form);
            }

            positions.add(position);

        }

        return positions;
    }

}
