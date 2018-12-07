package mn.compassmate.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

public class VtfmsFrameDecoder extends FrameDecoder {

    @Override
    protected Object decode(
            ChannelHandlerContext ctx, Channel channel, ChannelBuffer buf) throws Exception {

        int endIndex = buf.indexOf(buf.readerIndex(), buf.writerIndex(), (byte) ')');
        if (endIndex > 0) {
            endIndex += 1 + 3;
            if (buf.writerIndex() >= endIndex) {
                return buf.readBytes(endIndex - buf.readerIndex());
            }
        }

        return null;
    }

}
