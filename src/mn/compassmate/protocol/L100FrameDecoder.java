package mn.compassmate.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

public class L100FrameDecoder extends FrameDecoder {

    @Override
    protected Object decode(
            ChannelHandlerContext ctx, Channel channel, ChannelBuffer buf) throws Exception {

        if (buf.readableBytes() < 80) {
            return null;
        }

        int index = buf.indexOf(buf.readerIndex(), buf.writerIndex(), (byte) 0x02);
        if (index == -1) {
            index = buf.indexOf(buf.readerIndex(), buf.writerIndex(), (byte) 0x04);
            if (index == -1) {
                return null;
            }
        }

        index += 2; // checksum

        if (buf.readableBytes() >= index - buf.readerIndex()) {
            return buf.readBytes(index - buf.readerIndex());
        }

        return null;
    }

}
