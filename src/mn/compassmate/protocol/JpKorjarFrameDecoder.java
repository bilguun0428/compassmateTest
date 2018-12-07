package mn.compassmate.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

public class JpKorjarFrameDecoder extends FrameDecoder {

    @Override
    protected Object decode(
            ChannelHandlerContext ctx, Channel channel, ChannelBuffer buf) throws Exception {

        if (buf.readableBytes() < 80) {
            return null;
        }

        int spaceIndex = buf.indexOf(buf.readerIndex(), buf.writerIndex(), (byte) ' ');
        if (spaceIndex == -1) {
            return null;
        }

        int endIndex = buf.indexOf(spaceIndex, buf.writerIndex(), (byte) ',');
        if (endIndex == -1) {
            return null;
        }

        return buf.readBytes(endIndex + 1);
    }

}
