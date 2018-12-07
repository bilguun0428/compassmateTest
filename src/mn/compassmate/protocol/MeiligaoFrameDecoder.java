package mn.compassmate.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

public class MeiligaoFrameDecoder extends FrameDecoder {

    private static final int MESSAGE_HEADER = 4;

    @Override
    protected Object decode(
            ChannelHandlerContext ctx, Channel channel, ChannelBuffer buf) throws Exception {

        // Strip not '$' (0x24) bytes from the beginning
        while (buf.readable() && buf.getUnsignedByte(buf.readerIndex()) != 0x24) {
            buf.readByte();
        }

        // Check length and return buffer
        if (buf.readableBytes() >= MESSAGE_HEADER) {
            int length = buf.getUnsignedShort(buf.readerIndex() + 2);
            if (buf.readableBytes() >= length) {
                return buf.readBytes(length);
            }
        }

        return null;
    }

}
