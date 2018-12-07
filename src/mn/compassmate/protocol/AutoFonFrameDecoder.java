package mn.compassmate.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

public class AutoFonFrameDecoder extends FrameDecoder {

    @Override
    protected Object decode(
            ChannelHandlerContext ctx,
            Channel channel,
            ChannelBuffer buf) throws Exception {

        // Check minimum length
        if (buf.readableBytes() < 12) {
            return null;
        }

        int length;
        switch (buf.getUnsignedByte(buf.readerIndex())) {
            case AutoFonProtocolDecoder.MSG_LOGIN:
                length = 12;
                break;
            case AutoFonProtocolDecoder.MSG_LOCATION:
                length = 78;
                break;
            case AutoFonProtocolDecoder.MSG_HISTORY:
                length = 257;
                break;
            case AutoFonProtocolDecoder.MSG_45_LOGIN:
                length = 19;
                break;
            case AutoFonProtocolDecoder.MSG_45_LOCATION:
                length = 34;
                break;
            default:
                length = 0;
                break;
        }

        // Check length and return buffer
        if (length != 0 && buf.readableBytes() >= length) {
            return buf.readBytes(length);
        }

        return null;
    }

}
