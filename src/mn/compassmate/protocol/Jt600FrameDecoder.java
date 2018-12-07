package mn.compassmate.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

import java.text.ParseException;

public class Jt600FrameDecoder extends FrameDecoder {

    @Override
    protected Object decode(
            ChannelHandlerContext ctx, Channel channel, ChannelBuffer buf) throws Exception {

        if (buf.readableBytes() < 10) {
            return null;
        }

        char type = (char) buf.getByte(buf.readerIndex());

        if (type == '$') {
            boolean longFormat = buf.getUnsignedByte(buf.readerIndex() + 1) == 0x75;
            int length = buf.getUnsignedShort(buf.readerIndex() + (longFormat ? 8 : 7)) + 10;
            if (length >= buf.readableBytes()) {
                return buf.readBytes(length);
            }
        } else if (type == '(') {
            int endIndex = buf.indexOf(buf.readerIndex(), buf.writerIndex(), (byte) ')');
            if (endIndex != -1) {
                return buf.readBytes(endIndex + 1);
            }
        } else {
            throw new ParseException(null, 0); // unknown message
        }

        return null;
    }

}
