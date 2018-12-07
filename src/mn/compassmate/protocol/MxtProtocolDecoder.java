package mn.compassmate.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import mn.compassmate.BaseProtocolDecoder;
import mn.compassmate.DeviceSession;
import mn.compassmate.helper.BitUtil;
import mn.compassmate.helper.Checksum;
import mn.compassmate.helper.DateBuilder;
import mn.compassmate.helper.UnitsConverter;
import mn.compassmate.model.Position;

import java.net.SocketAddress;
import java.nio.ByteOrder;

public class MxtProtocolDecoder extends BaseProtocolDecoder {

    public MxtProtocolDecoder(MxtProtocol protocol) {
        super(protocol);
    }

    public static final int MSG_ACK = 0x02;
    public static final int MSG_NACK = 0x03;
    public static final int MSG_POSITION = 0x31;

    private static void sendResponse(Channel channel, int device, long id, int crc) {
        if (channel != null) {
            ChannelBuffer response = ChannelBuffers.dynamicBuffer(ByteOrder.LITTLE_ENDIAN, 0);
            response.writeByte(device);
            response.writeByte(MSG_ACK);
            response.writeInt((int) id);
            response.writeShort(crc);
            response.writeShort(Checksum.crc16(
                    Checksum.CRC16_XMODEM, response.toByteBuffer()));

            ChannelBuffer encoded = ChannelBuffers.dynamicBuffer();
            encoded.writeByte(0x01); // header
            while (response.readable()) {
                int b = response.readByte();
                if (b == 0x01 || b == 0x04 || b == 0x10 || b == 0x11 || b == 0x13) {
                    encoded.writeByte(0x10);
                    b += 0x20;
                }
                encoded.writeByte(b);
            }
            encoded.writeByte(0x04); // ending
            channel.write(encoded);
        }
    }

    @Override
    protected Object decode(
            Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {

        ChannelBuffer buf = (ChannelBuffer) msg;

        buf.readUnsignedByte(); // start
        int device = buf.readUnsignedByte(); // device descriptor
        int type = buf.readUnsignedByte();

        long id = buf.readUnsignedInt();
        DeviceSession deviceSession = getDeviceSession(channel, remoteAddress, String.valueOf(id));
        if (deviceSession == null) {
            return null;
        }

        if (type == MSG_POSITION) {

            Position position = new Position();
            position.setProtocol(getProtocolName());
            position.setDeviceId(deviceSession.getDeviceId());

            buf.readUnsignedByte(); // protocol
            int infoGroups = buf.readUnsignedByte();

            position.set(Position.KEY_INDEX, buf.readUnsignedShort());

            DateBuilder dateBuilder = new DateBuilder().setDate(2000, 1, 1);

            long date = buf.readUnsignedInt();

            long days = BitUtil.from(date, 6 + 6 + 5);
            long hours = BitUtil.between(date, 6 + 6, 6 + 6 + 5);
            long minutes = BitUtil.between(date, 6, 6 + 6);
            long seconds = BitUtil.to(date, 6);

            dateBuilder.addMillis((((days * 24 + hours) * 60 + minutes) * 60 + seconds) * 1000);

            position.setTime(dateBuilder.getDate());

            position.setValid(true);
            position.setLatitude(buf.readInt() / 1000000.0);
            position.setLongitude(buf.readInt() / 1000000.0);

            long flags = buf.readUnsignedInt();
            position.set(Position.KEY_IGNITION, BitUtil.check(flags, 0));
            if (BitUtil.check(flags, 1)) {
                position.set(Position.KEY_ALARM, Position.ALARM_GENERAL);
            }
            position.set(Position.KEY_INPUT, BitUtil.between(flags, 2, 7));
            position.set(Position.KEY_OUTPUT, BitUtil.between(flags, 7, 10));
            position.setCourse(BitUtil.between(flags, 10, 13) * 45);
            //position.setValid(BitUtil.check(flags, 15));
            position.set(Position.KEY_CHARGE, BitUtil.check(flags, 20));

            position.setSpeed(UnitsConverter.knotsFromKph(buf.readUnsignedByte()));

            buf.readUnsignedByte(); // input mask

            if (BitUtil.check(infoGroups, 0)) {
                buf.skipBytes(8); // waypoints
            }

            if (BitUtil.check(infoGroups, 1)) {
                buf.skipBytes(8); // wireless accessory
            }

            if (BitUtil.check(infoGroups, 2)) {
                position.set(Position.KEY_SATELLITES, buf.readUnsignedByte());
                position.set(Position.KEY_HDOP, buf.readUnsignedByte());
                position.setAccuracy(buf.readUnsignedByte());
                position.set(Position.KEY_RSSI, buf.readUnsignedByte());
                buf.readUnsignedShort(); // time since boot
                position.set(Position.KEY_POWER, buf.readUnsignedByte());
                position.set(Position.PREFIX_TEMP + 1, buf.readByte());
            }

            if (BitUtil.check(infoGroups, 3)) {
                position.set(Position.KEY_ODOMETER, buf.readUnsignedInt());
            }

            if (BitUtil.check(infoGroups, 4)) {
                position.set(Position.KEY_HOURS, buf.readUnsignedInt());
            }

            if (BitUtil.check(infoGroups, 5)) {
                buf.readUnsignedInt(); // reason
            }

            if (BitUtil.check(infoGroups, 6)) {
                position.set(Position.KEY_POWER, buf.readUnsignedShort() * 0.001);
                position.set(Position.KEY_BATTERY, buf.readUnsignedShort());
            }

            if (BitUtil.check(infoGroups, 7)) {
                position.set(Position.KEY_DRIVER_UNIQUE_ID, String.valueOf(buf.readUnsignedInt()));
            }

            buf.readerIndex(buf.writerIndex() - 3);
            sendResponse(channel, device, id, buf.readUnsignedShort());

            return position;
        }

        return null;
    }

}
