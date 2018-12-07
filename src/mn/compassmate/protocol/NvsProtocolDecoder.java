package mn.compassmate.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import mn.compassmate.BaseProtocolDecoder;
import mn.compassmate.DeviceSession;
import mn.compassmate.helper.UnitsConverter;
import mn.compassmate.model.Position;

import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class NvsProtocolDecoder extends BaseProtocolDecoder {

    public NvsProtocolDecoder(NvsProtocol protocol) {
        super(protocol);
    }

    private void sendResponse(Channel channel, String response) {
        if (channel != null) {
            channel.write(ChannelBuffers.copiedBuffer(response, StandardCharsets.US_ASCII));
        }
    }

    @Override
    protected Object decode(
            Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {

        ChannelBuffer buf = (ChannelBuffer) msg;


        if (buf.getUnsignedByte(buf.readerIndex()) == 0) {

            buf.readUnsignedShort(); // length

            String imei = buf.toString(buf.readerIndex(), 15, StandardCharsets.US_ASCII);

            if (getDeviceSession(channel, remoteAddress, imei) != null) {
                sendResponse(channel, "OK");
            } else {
                sendResponse(channel, "NO01");
            }

        } else {

            DeviceSession deviceSession = getDeviceSession(channel, remoteAddress);
            if (deviceSession == null) {
                return null;
            }

            List<Position> positions = new LinkedList<>();

            buf.skipBytes(4); // marker
            buf.readUnsignedShort(); // length
            buf.readLong(); // imei
            buf.readUnsignedByte(); // codec
            int count = buf.readUnsignedByte();

            for (int i = 0; i < count; i++) {
                Position position = new Position();
                position.setProtocol(getProtocolName());
                position.setDeviceId(deviceSession.getDeviceId());

                position.setTime(new Date(buf.readUnsignedInt() * 1000));

                position.set("reason", buf.readUnsignedByte());

                position.setLongitude(buf.readInt() / 10000000.0);
                position.setLatitude(buf.readInt() / 10000000.0);
                position.setAltitude(buf.readShort());
                position.setCourse(buf.readUnsignedShort());

                position.set(Position.KEY_SATELLITES, buf.readUnsignedByte());

                position.setSpeed(UnitsConverter.knotsFromKph(buf.readUnsignedShort()));
                position.setValid(buf.readUnsignedByte() != 0);

                buf.readUnsignedByte(); // used systems

                buf.readUnsignedByte(); // cause element id

                // Read 1 byte data
                int cnt = buf.readUnsignedByte();
                for (int j = 0; j < cnt; j++) {
                    position.set(Position.PREFIX_IO + buf.readUnsignedByte(), buf.readUnsignedByte());
                }

                // Read 2 byte data
                cnt = buf.readUnsignedByte();
                for (int j = 0; j < cnt; j++) {
                    position.set(Position.PREFIX_IO + buf.readUnsignedByte(), buf.readUnsignedShort());
                }

                // Read 4 byte data
                cnt = buf.readUnsignedByte();
                for (int j = 0; j < cnt; j++) {
                    position.set(Position.PREFIX_IO + buf.readUnsignedByte(), buf.readUnsignedInt());
                }

                // Read 8 byte data
                cnt = buf.readUnsignedByte();
                for (int j = 0; j < cnt; j++) {
                    position.set(Position.PREFIX_IO + buf.readUnsignedByte(), buf.readLong());
                }

                positions.add(position);
            }

            return positions;

        }

        return null;
    }

}
