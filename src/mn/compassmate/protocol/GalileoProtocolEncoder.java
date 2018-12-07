package mn.compassmate.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import mn.compassmate.BaseProtocolEncoder;
import mn.compassmate.helper.Checksum;
import mn.compassmate.helper.Log;
import mn.compassmate.model.Command;

import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class GalileoProtocolEncoder extends BaseProtocolEncoder {

    private ChannelBuffer encodeText(String uniqueId, String text) {

        ChannelBuffer buf = ChannelBuffers.dynamicBuffer(ByteOrder.LITTLE_ENDIAN, 256);

        buf.writeByte(0x01);
        buf.writeShort(uniqueId.length() + text.length() + 11); // TODO

        buf.writeByte(0x03); // imei tag
        buf.writeBytes(uniqueId.getBytes(StandardCharsets.US_ASCII));

        buf.writeByte(0x04); // device id tag
        buf.writeShort(0); // not needed if imei provided

        buf.writeByte(0xE0); // index tag
        buf.writeInt(0); // index

        buf.writeByte(0xE1); // command text tag
        buf.writeByte(text.length());
        buf.writeBytes(text.getBytes(StandardCharsets.US_ASCII));

        buf.writeShort(Checksum.crc16(Checksum.CRC16_MODBUS, buf.toByteBuffer(0, buf.writerIndex())));

        return buf;
    }

    @Override
    protected Object encodeCommand(Command command) {

        switch (command.getType()) {
            case Command.TYPE_CUSTOM:
                return encodeText(getUniqueId(command.getDeviceId()), command.getString(Command.KEY_DATA));
            case Command.TYPE_OUTPUT_CONTROL:
                return encodeText(getUniqueId(command.getDeviceId()),
                        "Out " + command.getInteger(Command.KEY_INDEX) + "," + command.getString(Command.KEY_DATA));
            default:
                Log.warning(new UnsupportedOperationException(command.getType()));
                break;
        }

        return null;
    }

}
