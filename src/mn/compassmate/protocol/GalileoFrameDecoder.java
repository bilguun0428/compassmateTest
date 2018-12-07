package mn.compassmate.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

public class GalileoFrameDecoder extends FrameDecoder {

    private static final int MESSAGE_MINIMUM_LENGTH = 5;

    @Override
    protected Object decode(
            ChannelHandlerContext ctx,
            Channel channel,
            ChannelBuffer buf) throws Exception {

        // Check minimum length
        if (buf.readableBytes() < MESSAGE_MINIMUM_LENGTH) {
            return null;
        }

        // Read packet
        int length = buf.getUnsignedShort(buf.readerIndex() + 1) & 0x7fff;
        if (buf.readableBytes() >= (length + MESSAGE_MINIMUM_LENGTH)) {
            return buf.readBytes(length + MESSAGE_MINIMUM_LENGTH);
        }

        return null;
    }

}
