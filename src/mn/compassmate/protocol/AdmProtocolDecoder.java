package mn.compassmate.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import mn.compassmate.BaseProtocolDecoder;
import mn.compassmate.DeviceSession;
import mn.compassmate.helper.BitUtil;
import mn.compassmate.helper.UnitsConverter;
import mn.compassmate.model.Position;

import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class AdmProtocolDecoder extends BaseProtocolDecoder {

    public AdmProtocolDecoder(AdmProtocol protocol) {
        super(protocol);
    }

    public static final int MSG_IMEI = 0x03;
    public static final int MSG_PHOTO = 0x0A;
    public static final int MSG_ADM5 = 0x01;

    @Override
    protected Object decode(
            Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {

        ChannelBuffer buf = (ChannelBuffer) msg;

        buf.readUnsignedShort(); // device id
        buf.readUnsignedByte(); // length

        int type = buf.readUnsignedByte();

        DeviceSession deviceSession;
        if (type == MSG_IMEI) {
            deviceSession = getDeviceSession(
                    channel, remoteAddress, buf.readBytes(15).toString(StandardCharsets.US_ASCII));
        } else {
            deviceSession = getDeviceSession(channel, remoteAddress);
        }

        if (deviceSession == null) {
            return null;
        }

        if (BitUtil.to(type, 2) == 0) {

            Position position = new Position();
            position.setProtocol(getProtocolName());
            position.setDeviceId(deviceSession.getDeviceId());

            position.set(Position.KEY_VERSION_FW, buf.readUnsignedByte());
            buf.readUnsignedShort(); // index

            position.set(Position.KEY_STATUS, buf.readUnsignedShort());

            position.setValid(true);
            position.setLatitude(buf.readFloat());
            position.setLongitude(buf.readFloat());
            position.setCourse(buf.readUnsignedShort() * 0.1);
            position.setSpeed(UnitsConverter.knotsFromKph(buf.readUnsignedShort() * 0.1));

            position.set(Position.KEY_ACCELERATION, buf.readUnsignedByte());

            position.setAltitude(buf.readUnsignedShort());

            position.set(Position.KEY_HDOP, buf.readUnsignedByte() * 0.1);
            position.set(Position.KEY_SATELLITES, buf.readUnsignedByte() & 0x0f);

            position.setTime(new Date(buf.readUnsignedInt() * 1000));

            position.set(Position.KEY_POWER, buf.readUnsignedShort());
            position.set(Position.KEY_BATTERY, buf.readUnsignedShort());

            if (BitUtil.check(type, 2)) {
                buf.skipBytes(4);
            }

            if (BitUtil.check(type, 3)) {
                buf.skipBytes(12);
            }

            if (BitUtil.check(type, 4)) {
                buf.skipBytes(8);
            }

            if (BitUtil.check(type, 5)) {
                buf.skipBytes(9);
            }

            if (BitUtil.check(type, 6)) {
                buf.skipBytes(buf.getUnsignedByte(buf.readerIndex()));
            }

            if (BitUtil.check(type, 7)) {
                position.set(Position.KEY_ODOMETER, buf.readUnsignedInt());
            }

            return position;
        }

        return null;
    }

}
