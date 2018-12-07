package mn.compassmate.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

public class NvsFrameDecoder extends FrameDecoder {

    @Override
    protected Object decode(
            ChannelHandlerContext ctx,
            Channel channel,
            ChannelBuffer buf) throws Exception {

        if (buf.readableBytes() < 4 + 2) {
            return null;
        }

        int length;
        if (buf.getUnsignedByte(buf.readerIndex()) == 0) {
            length = 2 + buf.getUnsignedShort(buf.readerIndex());
        } else {
            length = 4 + 2 + buf.getUnsignedShort(buf.readerIndex() + 4) + 2;
        }

        if (buf.readableBytes() >= length) {
            return buf.readBytes(length);
        }

        return null;
    }

}
