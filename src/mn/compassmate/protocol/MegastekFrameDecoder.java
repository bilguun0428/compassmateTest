package mn.compassmate.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import mn.compassmate.helper.StringFinder;

import java.nio.charset.StandardCharsets;

public class MegastekFrameDecoder extends FrameDecoder {

    @Override
    protected Object decode(
            ChannelHandlerContext ctx, Channel channel, ChannelBuffer buf) throws Exception {

        if (buf.readableBytes() < 10) {
            return null;
        }

        if (Character.isDigit(buf.getByte(buf.readerIndex()))) {
            int length = 4 + Integer.parseInt(buf.toString(buf.readerIndex(), 4, StandardCharsets.US_ASCII));
            if (buf.readableBytes() >= length) {
                return buf.readBytes(length);
            }
        } else {
            while (buf.getByte(buf.readerIndex()) == '\r' || buf.getByte(buf.readerIndex()) == '\n') {
                buf.skipBytes(1);
            }
            int delimiter = buf.indexOf(buf.readerIndex(), buf.writerIndex(), new StringFinder("\r\n"));
            if (delimiter == -1) {
                delimiter = buf.indexOf(buf.readerIndex(), buf.writerIndex(), (byte) '!');
            }
            if (delimiter != -1) {
                ChannelBuffer result = buf.readBytes(delimiter - buf.readerIndex());
                buf.skipBytes(1);
                return result;
            }
        }

        return null;
    }

}
