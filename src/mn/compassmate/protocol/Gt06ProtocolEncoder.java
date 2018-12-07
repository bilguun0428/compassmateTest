package mn.compassmate.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import mn.compassmate.BaseProtocolEncoder;
import mn.compassmate.Context;
import mn.compassmate.helper.Checksum;
import mn.compassmate.helper.Log;
import mn.compassmate.model.Command;

import java.nio.charset.StandardCharsets;

public class Gt06ProtocolEncoder extends BaseProtocolEncoder {

    private ChannelBuffer encodeContent(String content) {

        ChannelBuffer buf = ChannelBuffers.dynamicBuffer();

        buf.writeByte(0x78);
        buf.writeByte(0x78);

        buf.writeByte(1 + 1 + 4 + content.length() + 2 + 2); // message length

        buf.writeByte(0x80); // message type

        buf.writeByte(4 + content.length()); // command length
        buf.writeInt(0);
        buf.writeBytes(content.getBytes(StandardCharsets.US_ASCII)); // command

        buf.writeShort(0); // message index

        buf.writeShort(Checksum.crc16(Checksum.CRC16_X25, buf.toByteBuffer(2, buf.writerIndex() - 2)));

        buf.writeByte('\r');
        buf.writeByte('\n');

        return buf;
    }

    @Override
    protected Object encodeCommand(Command command) {

        boolean alternative = Context.getIdentityManager().lookupAttributeBoolean(
                command.getDeviceId(), "gt06.alternative", false, true);

        switch (command.getType()) {
            case Command.TYPE_ENGINE_STOP:
                return encodeContent(alternative ? "DYD,123456#" : "Relay,1#");
            case Command.TYPE_ENGINE_RESUME:
                return encodeContent(alternative ? "HFYD,123456#" : "Relay,0#");
            case Command.TYPE_CUSTOM:
                return encodeContent(command.getString(Command.KEY_DATA));
            default:
                Log.warning(new UnsupportedOperationException(command.getType()));
                break;
        }

        return null;
    }

}
