package mn.compassmate.database;

import org.jboss.netty.channel.Channel;
import mn.compassmate.Protocol;
import mn.compassmate.model.Command;

import java.net.SocketAddress;

public class ActiveDevice {

    private final long deviceId;
    private final Protocol protocol;
    private final Channel channel;
    private final SocketAddress remoteAddress;

    public ActiveDevice(long deviceId, Protocol protocol, Channel channel, SocketAddress remoteAddress) {
        this.deviceId = deviceId;
        this.protocol = protocol;
        this.channel = channel;
        this.remoteAddress = remoteAddress;
    }

    public Channel getChannel() {
        return channel;
    }

    public long getDeviceId() {
        return deviceId;
    }

    public void sendCommand(Command command) {
        protocol.sendDataCommand(this, command);
    }

    public void write(Object message) {
        getChannel().write(message, remoteAddress);
    }

}
