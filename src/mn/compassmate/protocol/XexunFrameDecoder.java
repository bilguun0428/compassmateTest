package mn.compassmate.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import mn.compassmate.helper.StringFinder;

public class XexunFrameDecoder extends FrameDecoder {

    @Override
    protected Object decode(
            ChannelHandlerContext ctx, Channel channel, ChannelBuffer buf) throws Exception {

        if (buf.readableBytes() < 80) {
            return null;
        }

        int beginIndex = buf.indexOf(buf.readerIndex(), buf.writerIndex(), new StringFinder("GPRMC"));
        if (beginIndex == -1) {
            beginIndex = buf.indexOf(buf.readerIndex(), buf.writerIndex(), new StringFinder("GNRMC"));
            if (beginIndex == -1) {
                return null;
            }
        }

        int identifierIndex = buf.indexOf(beginIndex, buf.writerIndex(), new StringFinder("imei:"));
        if (identifierIndex == -1) {
            return null;
        }

        int endIndex = buf.indexOf(identifierIndex, buf.writerIndex(), (byte) ',');
        if (endIndex == -1) {
            return null;
        }

        buf.skipBytes(beginIndex - buf.readerIndex());

        return buf.readBytes(endIndex - beginIndex + 1);
    }

}
