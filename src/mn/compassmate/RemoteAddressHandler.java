package mn.compassmate;

import org.jboss.netty.channel.Channel;
import mn.compassmate.model.Position;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class RemoteAddressHandler extends ExtendedObjectDecoder {

    @Override
    protected Object decode(
            Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {

        String hostAddress = ((InetSocketAddress) remoteAddress).getAddress().getHostAddress();

        if (msg instanceof Position) {
            Position position = (Position) msg;
            position.set(Position.KEY_IP, hostAddress);
        }

        return msg;
    }

}
