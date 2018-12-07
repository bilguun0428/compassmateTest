package mn.compassmate.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import mn.compassmate.BaseProtocolEncoder;
import mn.compassmate.helper.Checksum;
import mn.compassmate.helper.Log;
import mn.compassmate.model.Command;

public class KhdProtocolEncoder extends BaseProtocolEncoder {

    public static final int MSG_CUT_OIL = 0x39;
    public static final int MSG_RESUME_OIL = 0x38;

    private ChannelBuffer encodeCommand(int command) {

        ChannelBuffer buf = ChannelBuffers.dynamicBuffer();

        buf.writeByte(0x29);
        buf.writeByte(0x29);

        buf.writeByte(command);
        buf.writeShort(6); // size

        buf.writeInt(0); // terminal id

        buf.writeByte(Checksum.xor(buf.toByteBuffer()));
        buf.writeByte(0x0D); // ending

        return buf;
    }

    @Override
    protected Object encodeCommand(Command command) {

        switch (command.getType()) {
            case Command.TYPE_ENGINE_STOP:
                return encodeCommand(MSG_CUT_OIL);
            case Command.TYPE_ENGINE_RESUME:
                return encodeCommand(MSG_RESUME_OIL);
            default:
                Log.warning(new UnsupportedOperationException(command.getType()));
                break;
        }

        return null;
    }

}
