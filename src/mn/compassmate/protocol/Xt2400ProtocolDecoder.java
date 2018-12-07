package mn.compassmate.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import mn.compassmate.BaseProtocolDecoder;
import mn.compassmate.Context;
import mn.compassmate.DeviceSession;
import mn.compassmate.helper.UnitsConverter;
import mn.compassmate.model.Position;

import javax.xml.bind.DatatypeConverter;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Xt2400ProtocolDecoder extends BaseProtocolDecoder {

    public Xt2400ProtocolDecoder(Xt2400Protocol protocol) {
        super(protocol);

        String config = Context.getConfig().getString(getProtocolName() + ".config");
        if (config != null) {
            setConfig(config);
        }
    }

    private static final Map<Integer, Integer> TAG_LENGTH_MAP = new HashMap<>();

    static {
        int[] l1 = {
                0x01, 0x02, 0x04, 0x0b, 0x0c, 0x0d, 0x12, 0x13,
                0x16, 0x17, 0x1c, 0x1f, 0x23, 0x2c, 0x2d, 0x30,
                0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38,
                0x39, 0x3a, 0x3b, 0x3c, 0x3d, 0x3e, 0x40, 0x41,
                0x53, 0x66, 0x69, 0x6a, 0x93, 0x94, 0x96
        };
        int[] l2 = {
                0x05, 0x09, 0x0a, 0x14, 0x15, 0x1d, 0x1e, 0x24,
                0x26, 0x42, 0x43, 0x44, 0x45, 0x46, 0x47, 0x48,
                0x49, 0x57, 0x58, 0x59, 0x5a, 0x6b, 0x6f, 0x7A,
                0x7B, 0x7C, 0x7d, 0x7E, 0x7F, 0x80, 0x81, 0x82,
                0x83, 0x84, 0x85, 0x86
        };
        int[] l4 = {
                0x03, 0x06, 0x07, 0x08, 0x0e, 0x0f, 0x10, 0x11,
                0x18, 0x19, 0x1a, 0x1b, 0x20, 0x21, 0x22, 0x2e,
                0x2f, 0x4a, 0x4b, 0x4c, 0x4d, 0x4e, 0x4f, 0x50,
                0x51, 0x52, 0x54, 0x55, 0x56, 0x5b, 0x5c, 0x5d,
                0x5e, 0x5f, 0x60, 0x61, 0x62, 0x68, 0x6e, 0x71,
                0x72, 0x74, 0x75, 0x76, 0x77, 0x78, 0x79, 0x87,
                0x88, 0x89, 0x8a, 0x8b, 0x8c, 0x8d
        };
        for (int i : l1) {
            TAG_LENGTH_MAP.put(i, 1);
        }
        for (int i : l2) {
            TAG_LENGTH_MAP.put(i, 2);
        }
        for (int i : l4) {
            TAG_LENGTH_MAP.put(i, 4);
        }
        TAG_LENGTH_MAP.put(0x95, 24);
    }

    private static int getTagLength(int tag) {
        Integer length = TAG_LENGTH_MAP.get(tag);
        if (length == null) {
            throw new IllegalArgumentException("Unknown tag: " + tag);
        }
        return length;
    }

    private Map<Short, byte[]> formats = new HashMap<>();

    public void setConfig(String configString) {
        Pattern pattern = Pattern.compile(":wycfg pcr\\[\\d+\\] ([0-9a-fA-F]{2})[0-9a-fA-F]{2}([0-9a-fA-F]+)");
        Matcher matcher = pattern.matcher(configString);
        while (matcher.find()) {
            formats.put(Short.parseShort(matcher.group(1), 16), DatatypeConverter.parseHexBinary(matcher.group(2)));
        }
    }

    @Override
    protected Object decode(
            Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {

        ChannelBuffer buf = (ChannelBuffer) msg;

        byte[] format = null;
        if (formats.size() > 1) {
            format = formats.get(buf.getUnsignedByte(buf.readerIndex()));
        } else if (!formats.isEmpty()) {
            format = formats.values().iterator().next();
        }

        if (format == null) {
            return null;
        }

        Position position = new Position();
        position.setProtocol(getProtocolName());

        for (byte tag : format) {
            switch ((int) tag) {
                case 0x03:
                    DeviceSession deviceSession = getDeviceSession(
                            channel, remoteAddress, String.valueOf(buf.readUnsignedInt()));
                    if (deviceSession == null) {
                        return null;
                    }
                    position.setDeviceId(deviceSession.getDeviceId());
                    break;
                case 0x04:
                    position.set(Position.KEY_EVENT, buf.readUnsignedByte());
                    break;
                case 0x05:
                    position.set(Position.KEY_INDEX, buf.readUnsignedShort());
                    break;
                case 0x06:
                    position.setTime(new Date(buf.readUnsignedInt() * 1000));
                    break;
                case 0x07:
                    position.setLatitude(buf.readInt() * 0.000001);
                    break;
                case 0x08:
                    position.setLongitude(buf.readInt() * 0.000001);
                    break;
                case 0x09:
                    position.setAltitude(buf.readShort() * 0.1);
                    break;
                case 0x0a:
                    position.setCourse(buf.readShort() * 0.1);
                    break;
                case 0x0b:
                    position.setSpeed(UnitsConverter.knotsFromKph(buf.readUnsignedByte()));
                    break;
                case 0x10:
                    position.set(Position.KEY_ODOMETER_TRIP, buf.readUnsignedInt());
                    break;
                case 0x12:
                    position.set(Position.KEY_HDOP, buf.readUnsignedByte() * 0.1);
                    break;
                case 0x13:
                    position.set(Position.KEY_SATELLITES, buf.readUnsignedByte());
                    break;
                case 0x14:
                    position.set(Position.KEY_RSSI, buf.readShort());
                    break;
                case 0x16:
                    position.set(Position.KEY_BATTERY, buf.readUnsignedByte() * 0.1);
                    break;
                case 0x17:
                    position.set(Position.KEY_POWER, buf.readUnsignedByte() * 0.1);
                    break;
                case 0x57:
                    position.set(Position.KEY_OBD_SPEED, UnitsConverter.knotsFromKph(buf.readUnsignedShort()));
                    break;
                case 0x65:
                    position.set(Position.KEY_VIN, buf.readBytes(17).toString(StandardCharsets.US_ASCII));
                    break;
                case 0x73:
                    position.set(Position.KEY_VERSION_FW, buf.readBytes(16).toString(StandardCharsets.US_ASCII).trim());
                    break;
                default:
                    buf.skipBytes(getTagLength(tag));
                    break;
            }
        }

        if (position.getLatitude() != 0 && position.getLongitude() != 0) {
            position.setValid(true);
        } else {
            getLastLocation(position, position.getDeviceTime());
        }

        return position;
    }

}
