package mn.compassmate.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

public class Vt200FrameDecoder extends FrameDecoder {

    @Override
    protected Object decode(
            ChannelHandlerContext ctx, Channel channel, ChannelBuffer buf) throws Exception {

        int endIndex = buf.indexOf(buf.readerIndex(), buf.writerIndex(), (byte) ')') + 1;
        if (endIndex > 0) {

            ChannelBuffer frame = ChannelBuffers.dynamicBuffer();

            while (buf.readerIndex() < endIndex) {
                int b = buf.readByte();
                if (b == '=') {
                    frame.writeByte(buf.readByte() ^ '=');
                } else {
                    frame.writeByte(b);
                }
            }

            return frame;

        }

        return null;
    }

}
