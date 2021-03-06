package mn.compassmate.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import mn.compassmate.helper.StringFinder;

import java.nio.charset.StandardCharsets;

public class TotemFrameDecoder extends FrameDecoder {

    @Override
    protected Object decode(
            ChannelHandlerContext ctx, Channel channel, ChannelBuffer buf) throws Exception {

        if (buf.readableBytes() < 10) {
            return null;
        }

        int beginIndex = buf.indexOf(buf.readerIndex(), buf.writerIndex(), new StringFinder("$$"));
        if (beginIndex == -1) {
            return null;
        } else if (beginIndex > buf.readerIndex()) {
            buf.readerIndex(beginIndex);
        }

        int length;

        int flagIndex = buf.indexOf(buf.readerIndex(), buf.writerIndex(), new StringFinder("AA"));
        if (flagIndex != -1 && flagIndex - beginIndex == 6) {
            length = Integer.parseInt(buf.toString(buf.readerIndex() + 2, 4, StandardCharsets.US_ASCII));
        } else {
            length = Integer.parseInt(buf.toString(buf.readerIndex() + 2, 2, StandardCharsets.US_ASCII), 16);
        }

        if (length <= buf.readableBytes()) {
            return buf.readBytes(length);
        }

        return null;
    }

}
