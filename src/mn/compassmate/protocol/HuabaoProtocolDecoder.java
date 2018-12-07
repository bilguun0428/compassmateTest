package mn.compassmate.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import mn.compassmate.BaseProtocolDecoder;
import mn.compassmate.DeviceSession;
import mn.compassmate.helper.BcdUtil;
import mn.compassmate.helper.BitUtil;
import mn.compassmate.helper.Checksum;
import mn.compassmate.helper.DateBuilder;
import mn.compassmate.helper.UnitsConverter;
import mn.compassmate.model.Position;

import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.TimeZone;

public class HuabaoProtocolDecoder extends BaseProtocolDecoder {

    public HuabaoProtocolDecoder(HuabaoProtocol protocol) {
        super(protocol);
    }

    public static final int MSG_GENERAL_RESPONSE = 0x8001;
    public static final int MSG_TERMINAL_REGISTER = 0x0100;
    public static final int MSG_TERMINAL_REGISTER_RESPONSE = 0x8100;
    public static final int MSG_TERMINAL_AUTH = 0x0102;
    public static final int MSG_LOCATION_REPORT = 0x0200;
    public static final int MSG_OIL_CONTROL = 0XA006;

    public static final int RESULT_SUCCESS = 0;

    public static ChannelBuffer formatMessage(int type, ChannelBuffer id, ChannelBuffer data) {
        ChannelBuffer buf = ChannelBuffers.dynamicBuffer();
        buf.writeByte(0x7e);
        buf.writeShort(type);
        buf.writeShort(data.readableBytes());
        buf.writeBytes(id);
        buf.writeShort(1); // index
        buf.writeBytes(data);
        buf.writeByte(Checksum.xor(buf.toByteBuffer(1, buf.readableBytes() - 1)));
        buf.writeByte(0x7e);
        return buf;
    }

    private void sendGeneralResponse(
            Channel channel, SocketAddress remoteAddress, ChannelBuffer id, int type, int index) {
        if (channel != null) {
            ChannelBuffer response = ChannelBuffers.dynamicBuffer();
            response.writeShort(index);
            response.writeShort(type);
            response.writeByte(RESULT_SUCCESS);
            channel.write(formatMessage(MSG_GENERAL_RESPONSE, id, response), remoteAddress);
        }
    }

    private String decodeAlarm(long value) {
        if (BitUtil.check(value, 0)) {
            return Position.ALARM_SOS;
        }
        if (BitUtil.check(value, 1)) {
            return Position.ALARM_OVERSPEED;
        }
        if (BitUtil.check(value, 5)) {
            return Position.ALARM_GPS_ANTENNA_CUT;
        }
        if (BitUtil.check(value, 4) || BitUtil.check(value, 9)
                || BitUtil.check(value, 10) || BitUtil.check(value, 11)) {
            return Position.ALARM_FAULT;
        }
        if (BitUtil.check(value, 8)) {
            return Position.ALARM_POWER_OFF;
        }
        if (BitUtil.check(value, 20)) {
            return Position.ALARM_GEOFENCE;
        }
        if (BitUtil.check(value, 29)) {
            return Position.ALARM_ACCIDENT;
        }
        return null;
    }

    @Override
    protected Object decode(
            Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {

        ChannelBuffer buf = (ChannelBuffer) msg;

        buf.readUnsignedByte(); // start marker
        int type = buf.readUnsignedShort();
        buf.readUnsignedShort(); // body length
        ChannelBuffer id = buf.readBytes(6); // phone number
        int index = buf.readUnsignedShort();

        DeviceSession deviceSession = getDeviceSession(channel, remoteAddress, ChannelBuffers.hexDump(id));
        if (deviceSession == null) {
            return null;
        }

        if (type == MSG_TERMINAL_REGISTER) {

            if (channel != null) {
                ChannelBuffer response = ChannelBuffers.dynamicBuffer();
                response.writeShort(index);
                response.writeByte(RESULT_SUCCESS);
                response.writeBytes("authentication".getBytes(StandardCharsets.US_ASCII));
                channel.write(formatMessage(MSG_TERMINAL_REGISTER_RESPONSE, id, response), remoteAddress);
            }

        } else if (type == MSG_TERMINAL_AUTH) {

            sendGeneralResponse(channel, remoteAddress, id, type, index);

        } else if (type == MSG_LOCATION_REPORT) {

            Position position = new Position();
            position.setProtocol(getProtocolName());
            position.setDeviceId(deviceSession.getDeviceId());

            position.set(Position.KEY_ALARM, decodeAlarm(buf.readUnsignedInt()));

            int flags = buf.readInt();

            position.set(Position.KEY_IGNITION, BitUtil.check(flags, 0));

            position.setValid(BitUtil.check(flags, 1));

            double lat = buf.readUnsignedInt() * 0.000001;
            double lon = buf.readUnsignedInt() * 0.000001;

            if (BitUtil.check(flags, 2)) {
                position.setLatitude(-lat);
            } else {
                position.setLatitude(lat);
            }

            if (BitUtil.check(flags, 3)) {
                position.setLongitude(-lon);
            } else {
                position.setLongitude(lon);
            }

            position.setAltitude(buf.readShort());
            position.setSpeed(UnitsConverter.knotsFromKph(buf.readUnsignedShort() * 0.1));
            position.setCourse(buf.readUnsignedShort());

            DateBuilder dateBuilder = new DateBuilder(TimeZone.getTimeZone("GMT+8"))
                    .setYear(BcdUtil.readInteger(buf, 2))
                    .setMonth(BcdUtil.readInteger(buf, 2))
                    .setDay(BcdUtil.readInteger(buf, 2))
                    .setHour(BcdUtil.readInteger(buf, 2))
                    .setMinute(BcdUtil.readInteger(buf, 2))
                    .setSecond(BcdUtil.readInteger(buf, 2));
            position.setTime(dateBuilder.getDate());

            // additional information

            return position;

        }

        return null;
    }

}