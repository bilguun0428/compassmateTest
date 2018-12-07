package mn.compassmate.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import mn.compassmate.BaseProtocolDecoder;

import java.net.SocketAddress;

public class Gl200ProtocolDecoder extends BaseProtocolDecoder {

    private final Gl200TextProtocolDecoder textProtocolDecoder;
    private final Gl200BinaryProtocolDecoder binaryProtocolDecoder;

    public Gl200ProtocolDecoder(Gl200Protocol protocol) {
        super(protocol);
        textProtocolDecoder = new Gl200TextProtocolDecoder(protocol);
        binaryProtocolDecoder = new Gl200BinaryProtocolDecoder(protocol);
    }

    @Override
    protected Object decode(
            Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {

        ChannelBuffer buf = (ChannelBuffer) msg;

        if (Gl200FrameDecoder.isBinary(buf)) {
            return binaryProtocolDecoder.decode(channel, remoteAddress, msg);
        } else {
            return textProtocolDecoder.decode(channel, remoteAddress, msg);
        }
    }

}
