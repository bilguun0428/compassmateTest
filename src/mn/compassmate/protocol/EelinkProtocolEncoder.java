package mn.compassmate.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import mn.compassmate.BaseProtocolEncoder;
import mn.compassmate.helper.Log;
import mn.compassmate.model.Command;

import java.nio.charset.StandardCharsets;

public class EelinkProtocolEncoder extends BaseProtocolEncoder {

    private ChannelBuffer encodeContent(String content) {

        ChannelBuffer buf = ChannelBuffers.dynamicBuffer();

        buf.writeByte(0x67);
        buf.writeByte(0x67);
        buf.writeByte(EelinkProtocolDecoder.MSG_DOWNLINK);
        buf.writeShort(2 + 1 + 4 + content.length()); // length
        buf.writeShort(0); // index

        buf.writeByte(0x01); // command
        buf.writeInt(0); // server id
        buf.writeBytes(content.getBytes(StandardCharsets.UTF_8));

        return buf;
    }

    @Override
    protected Object encodeCommand(Command command) {

        switch (command.getType()) {
            case Command.TYPE_CUSTOM:
                return encodeContent(command.getString(Command.KEY_DATA));
            case Command.TYPE_ENGINE_STOP:
                return encodeContent("RELAY,1#");
            case Command.TYPE_ENGINE_RESUME:
                return encodeContent("RELAY,0#");
            case Command.TYPE_REBOOT_DEVICE:
                return encodeContent("RESET#");
            default:
                Log.warning(new UnsupportedOperationException(command.getType()));
                break;
        }

        return null;
    }

}
