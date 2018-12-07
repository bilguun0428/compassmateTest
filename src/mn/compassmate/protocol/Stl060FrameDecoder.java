package mn.compassmate.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import mn.compassmate.CharacterDelimiterFrameDecoder;

public class Stl060FrameDecoder extends CharacterDelimiterFrameDecoder {

    public Stl060FrameDecoder(int maxFrameLength) {
        super(maxFrameLength, '#');
    }

    @Override
    protected Object decode(
            ChannelHandlerContext ctx, Channel channel, ChannelBuffer buf) throws Exception {

        ChannelBuffer result = (ChannelBuffer) super.decode(ctx, channel, buf);

        if (result != null) {

            int index = result.indexOf(result.readerIndex(), result.writerIndex(), (byte) '$');
            if (index == -1) {
                return result;
            } else {
                result.skipBytes(index);
                return result.readBytes(result.readableBytes());
            }

        }

        return null;
    }

}
