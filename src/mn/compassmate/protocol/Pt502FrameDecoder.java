package mn.compassmate.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

public class Pt502FrameDecoder extends FrameDecoder {

    private static final int BINARY_HEADER = 5;

    @Override
    protected Object decode(
            ChannelHandlerContext ctx, Channel channel, ChannelBuffer buf) throws Exception {

        if (buf.readableBytes() < BINARY_HEADER) {
            return null;
        }

        if (buf.getUnsignedByte(buf.readerIndex()) == 0xbf) {
            buf.skipBytes(BINARY_HEADER);
        }

        int index = buf.indexOf(buf.readerIndex(), buf.writerIndex(), (byte) '\r');
        if (index != -1 && index + 1 < buf.writerIndex()) {
            ChannelBuffer result = buf.readBytes(index - buf.readerIndex());
            buf.skipBytes(2);
            return result;
        }

        return null;
    }

}
