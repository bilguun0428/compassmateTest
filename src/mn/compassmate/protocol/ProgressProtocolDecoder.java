package mn.compassmate.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import mn.compassmate.BaseProtocolDecoder;
import mn.compassmate.DeviceSession;
import mn.compassmate.helper.BitUtil;
import mn.compassmate.model.Position;

import java.net.SocketAddress;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class ProgressProtocolDecoder extends BaseProtocolDecoder {

    private long lastIndex;
    private long newIndex;

    public ProgressProtocolDecoder(ProgressProtocol protocol) {
        super(protocol);
    }

    public static final int MSG_NULL = 0;
    public static final int MSG_IDENT = 1;
    public static final int MSG_IDENT_FULL = 2;
    public static final int MSG_POINT = 10;
    public static final int MSG_LOG_SYNC = 100;
    public static final int MSG_LOGMSG = 101;
    public static final int MSG_TEXT = 102;
    public static final int MSG_ALARM = 200;
    public static final int MSG_ALARM_RECIEVED = 201;

    private void requestArchive(Channel channel) {
        if (lastIndex == 0) {
            lastIndex = newIndex;
        } else if (newIndex > lastIndex) {
            ChannelBuffer request = ChannelBuffers.directBuffer(ByteOrder.LITTLE_ENDIAN, 12);
            request.writeShort(MSG_LOG_SYNC);
            request.writeShort(4);
            request.writeInt((int) lastIndex);
            request.writeInt(0);
            channel.write(request);
        }
    }

    @Override
    protected Object decode(
            Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {

        ChannelBuffer buf = (ChannelBuffer) msg;
        int type = buf.readUnsignedShort();
        buf.readUnsignedShort(); // length

        if (type == MSG_IDENT || type == MSG_IDENT_FULL) {

            buf.readUnsignedInt(); // id
            int length = buf.readUnsignedShort();
            buf.skipBytes(length);
            length = buf.readUnsignedShort();
            buf.skipBytes(length);
            length = buf.readUnsignedShort();
            String imei = buf.readBytes(length).toString(StandardCharsets.US_ASCII);
            getDeviceSession(channel, remoteAddress, imei);

        } else if (type == MSG_POINT || type == MSG_ALARM || type == MSG_LOGMSG) {

            DeviceSession deviceSession = getDeviceSession(channel, remoteAddress);
            if (deviceSession == null) {
                return null;
            }

            List<Position> positions = new LinkedList<>();

            int recordCount = 1;
            if (type == MSG_LOGMSG) {
                recordCount = buf.readUnsignedShort();
            }

            for (int j = 0; j < recordCount; j++) {
                Position position = new Position();
                position.setProtocol(getProtocolName());
                position.setDeviceId(deviceSession.getDeviceId());

                if (type == MSG_LOGMSG) {
                    position.set(Position.KEY_ARCHIVE, true);
                    int subtype = buf.readUnsignedShort();
                    if (subtype == MSG_ALARM) {
                        position.set(Position.KEY_ALARM, Position.ALARM_GENERAL);
                    }
                    if (buf.readUnsignedShort() > buf.readableBytes()) {
                        lastIndex += 1;
                        break; // workaround for device bug
                    }
                    lastIndex = buf.readUnsignedInt();
                    position.set(Position.KEY_INDEX, lastIndex);
                } else {
                    newIndex = buf.readUnsignedInt();
                }

                position.setTime(new Date(buf.readUnsignedInt() * 1000));
                position.setLatitude(buf.readInt() * 180.0 / 0x7FFFFFFF);
                position.setLongitude(buf.readInt() * 180.0 / 0x7FFFFFFF);
                position.setSpeed(buf.readUnsignedInt() * 0.01);
                position.setCourse(buf.readUnsignedShort() * 0.01);
                position.setAltitude(buf.readUnsignedShort() * 0.01);

                int satellites = buf.readUnsignedByte();
                position.setValid(satellites >= 3);
                position.set(Position.KEY_SATELLITES, satellites);

                position.set(Position.KEY_RSSI, buf.readUnsignedByte());
                position.set(Position.KEY_ODOMETER, buf.readUnsignedInt());

                long extraFlags = buf.readLong();

                if (BitUtil.check(extraFlags, 0)) {
                    int count = buf.readUnsignedShort();
                    for (int i = 1; i <= count; i++) {
                        position.set(Position.PREFIX_ADC + i, buf.readUnsignedShort());
                    }
                }

                if (BitUtil.check(extraFlags, 1)) {
                    int size = buf.readUnsignedShort();
                    position.set("can", buf.toString(buf.readerIndex(), size, StandardCharsets.US_ASCII));
                    buf.skipBytes(size);
                }

                if (BitUtil.check(extraFlags, 2)) {
                    position.set("passenger",
                            ChannelBuffers.hexDump(buf.readBytes(buf.readUnsignedShort())));
                }

                if (type == MSG_ALARM) {
                    position.set(Position.KEY_ALARM, true);
                    byte[] response = {(byte) 0xC9, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
                    channel.write(ChannelBuffers.wrappedBuffer(response));
                }

                buf.readUnsignedInt(); // crc

                positions.add(position);
            }

            requestArchive(channel);

            return positions;
        }

        return null;
    }

}
