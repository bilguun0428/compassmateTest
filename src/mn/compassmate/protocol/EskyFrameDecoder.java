package mn.compassmate.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

public class EskyFrameDecoder extends FrameDecoder {

    @Override
    protected Object decode(
            ChannelHandlerContext ctx, Channel channel, ChannelBuffer buf) throws Exception {

        buf.readerIndex(buf.indexOf(buf.readerIndex(), buf.writerIndex(), (byte) 'E'));

        int endIndex = buf.indexOf(buf.readerIndex() + 1, buf.writerIndex(), (byte) 'E');
        if (endIndex > 0) {
            return buf.readBytes(endIndex - buf.readerIndex());
        } else {
            return buf.readBytes(buf.readableBytes()); // assume full frame
        }
    }

}
