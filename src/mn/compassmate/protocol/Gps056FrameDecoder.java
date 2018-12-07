package mn.compassmate.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

import java.nio.charset.StandardCharsets;

public class Gps056FrameDecoder extends FrameDecoder {

    private static final int MESSAGE_HEADER = 4;

    @Override
    protected Object decode(
            ChannelHandlerContext ctx, Channel channel, ChannelBuffer buf) throws Exception {

        if (buf.readableBytes() >= MESSAGE_HEADER) {
            int length = Integer.parseInt(buf.toString(2, 2, StandardCharsets.US_ASCII)) + 5;
            if (buf.readableBytes() >= length) {
                ChannelBuffer frame = buf.readBytes(length);
                while (buf.readable() && buf.getUnsignedByte(buf.readerIndex()) != '$') {
                    buf.readByte();
                }
                return frame;
            }
        }

        return null;
    }

}
