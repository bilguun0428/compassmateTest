package mn.compassmate.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import mn.compassmate.helper.StringFinder;

public class AtrackFrameDecoder extends FrameDecoder {

    private static final int KEEPALIVE_LENGTH = 12;

    @Override
    protected Object decode(
            ChannelHandlerContext ctx, Channel channel, ChannelBuffer buf) throws Exception {

        if (buf.readableBytes() >= 2) {

            if (buf.getUnsignedShort(buf.readerIndex()) == 0xfe02) {

                if (buf.readableBytes() >= KEEPALIVE_LENGTH) {
                    return buf.readBytes(KEEPALIVE_LENGTH);
                }

            } else if (buf.getUnsignedShort(buf.readerIndex()) == 0x4050) {

                if (buf.readableBytes() > 6) {
                    int length = buf.getUnsignedShort(buf.readerIndex() + 4) + 4 + 2;
                    if (buf.readableBytes() >= length) {
                        return buf.readBytes(length);
                    }
                }

            } else {

                int endIndex = buf.indexOf(buf.readerIndex(), buf.writerIndex(), new StringFinder("\r\n"));
                if (endIndex > 0) {
                    return buf.readBytes(endIndex - buf.readerIndex() + 2);
                }

            }

        }

        return null;
    }

}
