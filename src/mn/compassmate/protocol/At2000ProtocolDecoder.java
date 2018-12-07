package mn.compassmate.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import mn.compassmate.BaseProtocolDecoder;
import mn.compassmate.DeviceSession;
import mn.compassmate.helper.UnitsConverter;
import mn.compassmate.model.Position;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.net.SocketAddress;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class At2000ProtocolDecoder extends BaseProtocolDecoder {

    private static final int BLOCK_LENGTH = 16;

    public At2000ProtocolDecoder(At2000Protocol protocol) {
        super(protocol);
    }

    public static final int MSG_ACKNOWLEDGEMENT = 0x00;
    public static final int MSG_DEVICE_ID = 0x01;
    public static final int MSG_TRACK_REQUEST = 0x88;
    public static final int MSG_TRACK_RESPONSE = 0x89;
    public static final int MSG_SESSION_END = 0x0c;

    private Cipher cipher;

    private static void sendRequest(Channel channel) {
        if (channel != null) {
            ChannelBuffer response = ChannelBuffers.directBuffer(ByteOrder.LITTLE_ENDIAN, BLOCK_LENGTH);
            response.writeByte(MSG_TRACK_REQUEST);
            response.writeMedium(ChannelBuffers.swapMedium(0));
            response.writerIndex(BLOCK_LENGTH);
            channel.write(response);
        }
    }

    @Override
    protected Object decode(
            Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {

        ChannelBuffer buf = (ChannelBuffer) msg;

        if (buf.getUnsignedByte(buf.readerIndex()) == 0x01) {
            buf.readUnsignedByte(); // codec id
        }

        int type = buf.readUnsignedByte();
        buf.readUnsignedMedium(); // length
        buf.skipBytes(BLOCK_LENGTH - 1 - 3);

        if (type == MSG_DEVICE_ID) {

            String imei = buf.readBytes(15).toString(StandardCharsets.US_ASCII);
            if (getDeviceSession(channel, remoteAddress, imei) != null) {

                byte[] iv = new byte[BLOCK_LENGTH];
                buf.readBytes(iv);
                IvParameterSpec ivSpec = new IvParameterSpec(iv);

                SecretKeySpec keySpec = new SecretKeySpec(
                        DatatypeConverter.parseHexBinary("000102030405060708090a0b0c0d0e0f"), "AES");

                cipher = Cipher.getInstance("AES/CBC/NoPadding");
                cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

                byte[] data = new byte[BLOCK_LENGTH];
                buf.readBytes(data);
                cipher.update(data);

            }

        } else if (type == MSG_TRACK_RESPONSE) {

            DeviceSession deviceSession = getDeviceSession(channel, remoteAddress);
            if (deviceSession == null) {
                return null;
            }

            if (buf.capacity() <= BLOCK_LENGTH) {
                return null; // empty message
            }

            byte[] data = new byte[buf.capacity() - BLOCK_LENGTH];
            buf.readBytes(data);
            buf = ChannelBuffers.wrappedBuffer(ByteOrder.LITTLE_ENDIAN, cipher.update(data));

            List<Position> positions = new LinkedList<>();

            while (buf.readableBytes() >= 63) {

                Position position = new Position();
                position.setProtocol(getProtocolName());
                position.setDeviceId(deviceSession.getDeviceId());

                buf.readUnsignedShort(); // index
                buf.readUnsignedShort(); // reserved

                position.setValid(true);

                position.setTime(new Date(buf.readLong() * 1000));

                position.setLatitude(buf.readFloat());
                position.setLongitude(buf.readFloat());
                position.setAltitude(buf.readFloat());
                position.setSpeed(UnitsConverter.knotsFromKph(buf.readFloat()));
                position.setCourse(buf.readFloat());

                buf.readUnsignedInt(); // geozone event
                buf.readUnsignedInt(); // io events
                buf.readUnsignedInt(); // geozone value
                buf.readUnsignedInt(); // io values
                buf.readUnsignedShort(); // operator

                position.set(Position.PREFIX_ADC + 1, buf.readUnsignedShort());
                position.set(Position.PREFIX_ADC + 1, buf.readUnsignedShort());

                position.set(Position.KEY_POWER, buf.readUnsignedShort() * 0.001);

                buf.readUnsignedShort(); // cid
                position.set(Position.KEY_RSSI, buf.readUnsignedByte());
                buf.readUnsignedByte(); // current profile

                position.set(Position.KEY_BATTERY, buf.readUnsignedByte());
                position.set(Position.PREFIX_TEMP + 1, buf.readUnsignedByte());
                position.set(Position.KEY_SATELLITES, buf.readUnsignedByte());

                positions.add(position);

            }

            return positions;

        }

        if (type == MSG_DEVICE_ID) {
            sendRequest(channel);
        }

        return null;
    }

}
