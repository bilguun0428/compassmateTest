package mn.compassmate;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import mn.compassmate.model.Position;

import javax.xml.bind.DatatypeConverter;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

public abstract class ExtendedObjectDecoder implements ChannelUpstreamHandler {

    private void saveOriginal(Object decodedMessage, Object originalMessage) {
        if (Context.getConfig().getBoolean("database.saveOriginal") && decodedMessage instanceof Position) {
            Position position = (Position) decodedMessage;
            if (originalMessage instanceof ChannelBuffer) {
                ChannelBuffer buf = (ChannelBuffer) originalMessage;
                position.set(Position.KEY_ORIGINAL, ChannelBuffers.hexDump(buf, 0, buf.writerIndex()));
            } else if (originalMessage instanceof String) {
                position.set(Position.KEY_ORIGINAL, DatatypeConverter.printHexBinary(
                                ((String) originalMessage).getBytes(StandardCharsets.US_ASCII)));
            }
        }
    }

    @Override
    public void handleUpstream(
            ChannelHandlerContext ctx, ChannelEvent evt) throws Exception {
        if (!(evt instanceof MessageEvent)) {
            ctx.sendUpstream(evt);
            return;
        }

        MessageEvent e = (MessageEvent) evt;
        Object originalMessage = e.getMessage();
        Object decodedMessage = decode(e.getChannel(), e.getRemoteAddress(), originalMessage);
        onMessageEvent(e.getChannel(), e.getRemoteAddress(), originalMessage, decodedMessage);
        if (originalMessage == decodedMessage) {
            ctx.sendUpstream(evt);
        } else {
            if (decodedMessage == null) {
                decodedMessage = handleEmptyMessage(e.getChannel(), e.getRemoteAddress(), originalMessage);
            }
            if (decodedMessage != null) {
                if (decodedMessage instanceof Collection) {
                    for (Object o : (Collection) decodedMessage) {
                        saveOriginal(o, originalMessage);
                        Channels.fireMessageReceived(ctx, o, e.getRemoteAddress());
                    }
                } else {
                    saveOriginal(decodedMessage, originalMessage);
                    Channels.fireMessageReceived(ctx, decodedMessage, e.getRemoteAddress());
                }
            }
        }
    }

    protected void onMessageEvent(
            Channel channel, SocketAddress remoteAddress, Object originalMessage, Object decodedMessage) {
    }

    protected Object handleEmptyMessage(Channel channel, SocketAddress remoteAddress, Object msg) {
        return null;
    }

    protected abstract Object decode(Channel channel, SocketAddress remoteAddress, Object msg) throws Exception;

}
