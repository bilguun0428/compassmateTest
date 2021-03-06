package mn.compassmate.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import mn.compassmate.BaseProtocolDecoder;
import mn.compassmate.DeviceSession;
import mn.compassmate.helper.BitUtil;
import mn.compassmate.model.CellTower;
import mn.compassmate.model.Network;
import mn.compassmate.model.Position;

import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class ThinkRaceProtocolDecoder extends BaseProtocolDecoder {

    public ThinkRaceProtocolDecoder(ThinkRaceProtocol protocol) {
        super(protocol);
    }

    public static final int MSG_LOGIN = 0x80;
    public static final int MSG_GPS = 0x90;

    private static double convertCoordinate(long raw, boolean negative) {
        long degrees = raw / 1000000;
        double minutes = (raw % 1000000) * 0.0001;
        double result = degrees + minutes / 60;
        if (negative) {
            result = -result;
        }
        return result;
    }

    @Override
    protected Object decode(
            Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {

        ChannelBuffer buf = (ChannelBuffer) msg;

        buf.skipBytes(2); // header
        ChannelBuffer id = buf.readBytes(12);
        buf.readUnsignedByte(); // separator
        int type = buf.readUnsignedByte();
        buf.readUnsignedShort(); // length

        if (type == MSG_LOGIN) {

            int command = buf.readUnsignedByte(); // 0x00 - heartbeat

            if (command == 0x01) {
                String imei = buf.toString(buf.readerIndex(), 15, StandardCharsets.US_ASCII);
                DeviceSession deviceSession = getDeviceSession(channel, remoteAddress, imei);
                if (deviceSession != null && channel != null) {
                    ChannelBuffer response = ChannelBuffers.dynamicBuffer();
                    response.writeByte(0x48); response.writeByte(0x52); // header
                    response.writeBytes(id);
                    response.writeByte(0x2c); // separator
                    response.writeByte(type);
                    response.writeShort(0x0002); // length
                    response.writeShort(0x8000);
                    response.writeShort(0x0000); // checksum
                    channel.write(response);
                }
            }

        } else if (type == MSG_GPS) {

            DeviceSession deviceSession = getDeviceSession(channel, remoteAddress);
            if (deviceSession == null) {
                return null;
            }

            Position position = new Position();
            position.setProtocol(getProtocolName());
            position.setDeviceId(deviceSession.getDeviceId());

            position.setTime(new Date(buf.readUnsignedInt() * 1000));

            int flags = buf.readUnsignedByte();

            position.setValid(true);
            position.setLatitude(convertCoordinate(buf.readUnsignedInt(), !BitUtil.check(flags, 0)));
            position.setLongitude(convertCoordinate(buf.readUnsignedInt(), !BitUtil.check(flags, 1)));

            position.setSpeed(buf.readUnsignedByte());
            position.setCourse(buf.readUnsignedByte());

            position.setNetwork(new Network(
                    CellTower.fromLacCid(buf.readUnsignedShort(), buf.readUnsignedShort())));

            return position;

        }

        return null;
    }

}
