package mn.compassmate.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import mn.compassmate.helper.Log;

public class CellocatorFrameDecoder extends FrameDecoder {

    private static final int MESSAGE_MINIMUM_LENGTH = 15;

    @Override
    protected Object decode(
            ChannelHandlerContext ctx,
            Channel channel,
            ChannelBuffer buf) throws Exception {

        // Check minimum length
        int available = buf.readableBytes();
        if (available < MESSAGE_MINIMUM_LENGTH) {
            return null;
        }

        // Size depending on message type
        int length = 0;
        int type = buf.getUnsignedByte(4);
        switch (type) {
            case CellocatorProtocolDecoder.MSG_CLIENT_STATUS:
                length = 70;
                break;
            case CellocatorProtocolDecoder.MSG_CLIENT_PROGRAMMING:
                length = 31;
                break;
            case CellocatorProtocolDecoder.MSG_CLIENT_SERIAL_LOG:
                length = 70;
                break;
            case CellocatorProtocolDecoder.MSG_CLIENT_SERIAL:
                if (available >= 19) {
                    length = 19 + buf.getUnsignedShort(16);
                }
                break;
            case CellocatorProtocolDecoder.MSG_CLIENT_MODULAR:
                length = 15 + buf.getUnsignedByte(13);
                break;
            default:
                Log.warning(new UnsupportedOperationException(String.valueOf(type)));
                break;
        }

        // Read packet
        if (length > 0 && available >= length) {
            return buf.readBytes(length);
        }

        return null;
    }

}
