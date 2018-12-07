package mn.compassmate.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

public class UlbotechFrameDecoder extends FrameDecoder {

    @Override
    protected Object decode(
            ChannelHandlerContext ctx,
            Channel channel,
            ChannelBuffer buf) throws Exception {

        if (buf.readableBytes() < 2) {
            return null;
        }

        if (buf.getUnsignedByte(buf.readerIndex()) == 0xF8) {

            int index = buf.indexOf(buf.readerIndex() + 1, buf.writerIndex(), (byte) 0xF8);
            if (index != -1) {
                ChannelBuffer result = ChannelBuffers.buffer(index + 1 - buf.readerIndex());

                while (buf.readerIndex() <= index) {
                    int b = buf.readUnsignedByte();
                    if (b == 0xF7) {
                        int ext = buf.readUnsignedByte();
                        if (ext == 0x00) {
                            result.writeByte(0xF7);
                        } else if (ext == 0x0F) {
                            result.writeByte(0xF8);
                        }
                    } else {
                        result.writeByte(b);
                    }
                }

                return result;
            }

        } else {

            int index = buf.indexOf(buf.readerIndex(), buf.writerIndex(), (byte) '#');
            if (index != -1) {
                return buf.readBytes(index + 1 - buf.readerIndex());
            }

        }

        return null;
    }

}
