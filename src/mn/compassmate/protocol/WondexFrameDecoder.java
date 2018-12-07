package mn.compassmate.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import mn.compassmate.helper.StringFinder;

public class WondexFrameDecoder extends FrameDecoder {

    private static final int KEEP_ALIVE_LENGTH = 8;

    @Override
    protected Object decode(
            ChannelHandlerContext ctx, Channel channel, ChannelBuffer buf) throws Exception {

        if (buf.readableBytes() < KEEP_ALIVE_LENGTH) {
            return null;
        }

        if (buf.getUnsignedByte(buf.readerIndex()) == 0xD0) {

            // Send response
            ChannelBuffer frame = buf.readBytes(KEEP_ALIVE_LENGTH);
            if (channel != null) {
                channel.write(frame);
            }
            return frame;

        } else {

            int index = buf.indexOf(buf.readerIndex(), buf.writerIndex(), new StringFinder("\r\n"));
            if (index != -1) {
                ChannelBuffer frame = buf.readBytes(index - buf.readerIndex());
                buf.skipBytes(2);
                return frame;
            }

        }

        return null;
    }

}
