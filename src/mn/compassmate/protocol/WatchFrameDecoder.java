package mn.compassmate.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

import java.nio.charset.StandardCharsets;

public class WatchFrameDecoder extends FrameDecoder {

    public static final int MESSAGE_HEADER = 20;

    @Override
    protected Object decode(
            ChannelHandlerContext ctx, Channel channel, ChannelBuffer buf) throws Exception {

        if (buf.readableBytes() >= MESSAGE_HEADER) {
            ChannelBuffer lengthBuffer = ChannelBuffers.dynamicBuffer();
            buf.getBytes(buf.readerIndex() + MESSAGE_HEADER - 4 - 1, lengthBuffer, 4);
            int length = Integer.parseInt(lengthBuffer.toString(StandardCharsets.US_ASCII), 16) + MESSAGE_HEADER + 1;
            if (buf.readableBytes() >= length) {
                ChannelBuffer frame = ChannelBuffers.dynamicBuffer();
                int endIndex = buf.readerIndex() + length;
                while (buf.readerIndex() < endIndex) {
                    byte b = buf.readByte();
                    if (b == 0x7D) {
                        switch (buf.readByte()) {
                            case 0x01:
                                frame.writeByte(0x7D);
                                break;
                            case 0x02:
                                frame.writeByte(0x5B);
                                break;
                            case 0x03:
                                frame.writeByte(0x5D);
                                break;
                            case 0x04:
                                frame.writeByte(0x2C);
                                break;
                            case 0x05:
                                frame.writeByte(0x2A);
                                break;
                            default:
                                throw new IllegalArgumentException();
                        }
                    } else {
                        frame.writeByte(b);
                    }
                }
                return frame;
            }
        }

        return null;
    }

}
