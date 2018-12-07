package mn.compassmate.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

public class HuabaoFrameDecoder extends FrameDecoder {

    @Override
    protected Object decode(
            ChannelHandlerContext ctx, Channel channel, ChannelBuffer buf) throws Exception {

        if (buf.readableBytes() < 2) {
            return null;
        }

        int index = buf.indexOf(buf.readerIndex() + 1, buf.writerIndex(), (byte) 0x7e);
        if (index != -1) {
            ChannelBuffer result = ChannelBuffers.buffer(index + 1 - buf.readerIndex());

            while (buf.readerIndex() <= index) {
                int b = buf.readUnsignedByte();
                if (b == 0x7d) {
                    int ext = buf.readUnsignedByte();
                    if (ext == 0x01) {
                        result.writeByte(0x7d);
                    } else if (ext == 0x02) {
                        result.writeByte(0x7e);
                    }
                } else {
                    result.writeByte(b);
                }
            }

            return result;
        }

        return null;
    }

}
