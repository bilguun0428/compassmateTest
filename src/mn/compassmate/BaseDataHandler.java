package mn.compassmate;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;
import mn.compassmate.model.Position;

public abstract class BaseDataHandler extends OneToOneDecoder {

    @Override
    protected final Object decode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {

        if (msg instanceof Position) {
            return handlePosition((Position) msg);
        }

        return msg;
    }

    protected abstract Position handlePosition(Position position);

}
