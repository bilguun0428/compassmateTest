package mn.compassmate.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import mn.compassmate.BaseProtocolDecoder;
import mn.compassmate.DeviceSession;
import mn.compassmate.helper.BitUtil;
import mn.compassmate.helper.DateBuilder;
import mn.compassmate.helper.UnitsConverter;
import mn.compassmate.model.Position;

import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;

public class Gt02ProtocolDecoder extends BaseProtocolDecoder {

    public Gt02ProtocolDecoder(Gt02Protocol protocol) {
        super(protocol);
    }

    public static final int MSG_DATA = 0x10;
    public static final int MSG_HEARTBEAT = 0x1A;
    public static final int MSG_RESPONSE = 0x1C;

    @Override
    protected Object decode(
            Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {

        ChannelBuffer buf = (ChannelBuffer) msg;

        buf.skipBytes(2); // header
        buf.readByte(); // size

        Position position = new Position();
        position.setProtocol(getProtocolName());

        // Zero for location messages
        int power = buf.readUnsignedByte();
        int gsm = buf.readUnsignedByte();

        String imei = ChannelBuffers.hexDump(buf.readBytes(8)).substring(1);
        DeviceSession deviceSession = getDeviceSession(channel, remoteAddress, imei);
        if (deviceSession == null) {
            return null;
        }
        position.setDeviceId(deviceSession.getDeviceId());

        position.set(Position.KEY_INDEX, buf.readUnsignedShort());

        int type = buf.readUnsignedByte();

        if (type == MSG_HEARTBEAT) {

            getLastLocation(position, null);

            position.set(Position.KEY_POWER, power);
            position.set(Position.KEY_RSSI, gsm);

            if (channel != null) {
                byte[] response = {0x54, 0x68, 0x1A, 0x0D, 0x0A};
                channel.write(ChannelBuffers.wrappedBuffer(response));
            }

        } else if (type == MSG_DATA) {

            DateBuilder dateBuilder = new DateBuilder()
                    .setDate(buf.readUnsignedByte(), buf.readUnsignedByte(), buf.readUnsignedByte())
                    .setTime(buf.readUnsignedByte(), buf.readUnsignedByte(), buf.readUnsignedByte());
            position.setTime(dateBuilder.getDate());

            double latitude = buf.readUnsignedInt() / (60.0 * 30000.0);
            double longitude = buf.readUnsignedInt() / (60.0 * 30000.0);

            position.setSpeed(UnitsConverter.knotsFromKph(buf.readUnsignedByte()));
            position.setCourse(buf.readUnsignedShort());

            buf.skipBytes(3); // reserved

            long flags = buf.readUnsignedInt();
            position.setValid(BitUtil.check(flags, 0));
            if (!BitUtil.check(flags, 1)) {
                latitude = -latitude;
            }
            if (!BitUtil.check(flags, 2)) {
                longitude = -longitude;
            }

            position.setLatitude(latitude);
            position.setLongitude(longitude);

        } else if (type == MSG_RESPONSE) {

            getLastLocation(position, null);

            position.set(Position.KEY_RESULT,
                    buf.readBytes(buf.readUnsignedByte()).toString(StandardCharsets.US_ASCII));

        } else {

            return null;

        }

        return position;
    }

}
