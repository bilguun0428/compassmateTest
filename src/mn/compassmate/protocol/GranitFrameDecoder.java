package mn.compassmate.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import mn.compassmate.helper.StringFinder;

public class GranitFrameDecoder extends FrameDecoder {

    @Override
    protected Object decode(
            ChannelHandlerContext ctx, Channel channel, ChannelBuffer buf) throws Exception {

        int indexEnd = buf.indexOf(buf.readerIndex(), buf.writerIndex(), new StringFinder("\r\n"));
        if (indexEnd != -1) {
            int indexTilde = buf.indexOf(buf.readerIndex(), buf.writerIndex(), new StringFinder("~"));
            if (indexTilde != -1 && indexTilde < indexEnd) {
                int length = buf.getUnsignedShort(indexTilde + 1);
                indexEnd = buf.indexOf(indexTilde + 2 + length, buf.writerIndex(), new StringFinder("\r\n"));
                if (indexEnd == -1) {
                    return null;
                }
            }
            ChannelBuffer frame = buf.readBytes(indexEnd - buf.readerIndex());
            buf.skipBytes(2);
            return frame;
        }
        return null;
    }

}
