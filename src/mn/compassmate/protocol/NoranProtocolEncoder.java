package mn.compassmate.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import mn.compassmate.BaseProtocolEncoder;
import mn.compassmate.helper.Log;
import mn.compassmate.model.Command;

import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class NoranProtocolEncoder extends BaseProtocolEncoder {

    private ChannelBuffer encodeContent(String content) {

        ChannelBuffer buf = ChannelBuffers.buffer(ByteOrder.LITTLE_ENDIAN, 12 + 56);

        buf.writeBytes(
                ChannelBuffers.copiedBuffer(ByteOrder.LITTLE_ENDIAN, "\r\n*KW", StandardCharsets.US_ASCII));
        buf.writeByte(0);
        buf.writeShort(buf.capacity());
        buf.writeShort(NoranProtocolDecoder.MSG_CONTROL);
        buf.writeInt(0); // gis ip
        buf.writeShort(0); // gis port
        buf.writeBytes(content.getBytes(StandardCharsets.US_ASCII));
        buf.writerIndex(buf.writerIndex() + 50 - content.length());
        buf.writeBytes(
                ChannelBuffers.copiedBuffer(ByteOrder.LITTLE_ENDIAN, "\r\n", StandardCharsets.US_ASCII));

        return buf;
    }

    @Override
    protected Object encodeCommand(Command command) {

        switch (command.getType()) {
            case Command.TYPE_POSITION_SINGLE:
                return encodeContent("*KW,000,000,000000#");
            case Command.TYPE_POSITION_PERIODIC:
                int interval = command.getInteger(Command.KEY_FREQUENCY);
                return encodeContent("*KW,000,002,000000," + interval + "#");
            case Command.TYPE_POSITION_STOP:
                return encodeContent("*KW,000,002,000000,0#");
            case Command.TYPE_ENGINE_STOP:
                return encodeContent("*KW,000,007,000000,0#");
            case Command.TYPE_ENGINE_RESUME:
                return encodeContent("*KW,000,007,000000,1#");
            default:
                Log.warning(new UnsupportedOperationException(command.getType()));
                break;
        }

        return null;
    }

}
