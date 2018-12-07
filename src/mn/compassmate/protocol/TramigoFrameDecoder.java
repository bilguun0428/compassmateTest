package mn.compassmate.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;

import java.nio.ByteOrder;

public class TramigoFrameDecoder extends LengthFieldBasedFrameDecoder {

    public TramigoFrameDecoder() {
        super(1024, 6, 2, -8, 0);
    }

    @Override
    protected Object decode(
            ChannelHandlerContext ctx,
            Channel channel,
            ChannelBuffer buf) throws Exception {

        if (buf.readableBytes() < 20) {
            return null;
        }

        // Swap byte order for legacy protocol
        if (buf.getUnsignedByte(buf.readerIndex()) == 0x80) {
            int length = buf.readableBytes();
            byte[] bytes = new byte[length];
            buf.getBytes(buf.readerIndex(), bytes);

            ChannelBuffer result = (ChannelBuffer) super.decode(
                    ctx, channel, ChannelBuffers.wrappedBuffer(ByteOrder.LITTLE_ENDIAN, bytes));
            if (result != null) {
                buf.skipBytes(result.readableBytes());
            }
            return result;
        }

        return super.decode(ctx, channel, buf);
    }

}
