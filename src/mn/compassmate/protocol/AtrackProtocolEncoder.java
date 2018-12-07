package mn.compassmate.protocol;

import org.jboss.netty.buffer.ChannelBuffers;
import mn.compassmate.BaseProtocolEncoder;
import mn.compassmate.helper.Log;
import mn.compassmate.model.Command;

import java.nio.charset.StandardCharsets;

public class AtrackProtocolEncoder extends BaseProtocolEncoder {

    @Override
    protected Object encodeCommand(Command command) {

        switch (command.getType()) {
            case Command.TYPE_CUSTOM:
                return ChannelBuffers.copiedBuffer(
                        command.getString(Command.KEY_DATA) + "\r\n", StandardCharsets.US_ASCII);
            default:
                Log.warning(new UnsupportedOperationException(command.getType()));
                break;
        }

        return null;
    }

}
