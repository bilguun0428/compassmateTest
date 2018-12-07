package mn.compassmate.protocol;

import java.nio.charset.StandardCharsets;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import mn.compassmate.BaseProtocolEncoder;
import mn.compassmate.helper.Log;
import mn.compassmate.model.Command;

public class GranitProtocolEncoder extends BaseProtocolEncoder {
    @Override
    protected Object encodeCommand(Command command) {

        String commandString = "";

        switch (command.getType()) {
            case Command.TYPE_IDENTIFICATION:
                commandString = "BB+IDNT";
                break;
            case Command.TYPE_REBOOT_DEVICE:
                commandString = "BB+RESET";
                break;
            case Command.TYPE_POSITION_SINGLE:
                commandString = "BB+RRCD";
                break;
            default:
                Log.warning(new UnsupportedOperationException(command.getType()));
                return null;
        }
        if (!commandString.isEmpty()) {
            ChannelBuffer commandBuf = ChannelBuffers.dynamicBuffer();
            commandBuf.writeBytes(commandString.getBytes(StandardCharsets.US_ASCII));
            GranitProtocolDecoder.appendChecksum(commandBuf, commandString.length());
            return commandBuf;
        }
        return null;
    }

}
