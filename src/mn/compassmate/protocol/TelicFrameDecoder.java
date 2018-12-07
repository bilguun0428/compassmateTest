package mn.compassmate.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

public class TelicFrameDecoder extends FrameDecoder {

    @Override
    protected Object decode(
            ChannelHandlerContext ctx, Channel channel, ChannelBuffer buf) throws Exception {

        if (buf.readableBytes() < 4) {
            return null;
        }

        long length = buf.getUnsignedInt(buf.readerIndex());

        if (length < 1024) {
            if (buf.readableBytes() >= length + 4) {
                buf.readUnsignedInt();
                return buf.readBytes((int) length);
            }
        } else {
            int endIndex = buf.indexOf(buf.readerIndex(), buf.writerIndex(), (byte) 0);
            if (endIndex >= 0) {
                ChannelBuffer frame = buf.readBytes(endIndex - buf.readerIndex());
                buf.readByte();
                if (frame.readableBytes() > 0) {
                    return frame;
                }
            }
        }

        return null;
    }

}
