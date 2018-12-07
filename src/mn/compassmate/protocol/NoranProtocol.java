package mn.compassmate.protocol;

import org.jboss.netty.bootstrap.ConnectionlessBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import mn.compassmate.BaseProtocol;
import mn.compassmate.TrackerServer;
import mn.compassmate.model.Command;

import java.nio.ByteOrder;
import java.util.List;

public class NoranProtocol extends BaseProtocol {

    public NoranProtocol() {
        super("noran");
        setSupportedDataCommands(
                Command.TYPE_POSITION_SINGLE,
                Command.TYPE_POSITION_PERIODIC,
                Command.TYPE_POSITION_STOP,
                Command.TYPE_ENGINE_STOP,
                Command.TYPE_ENGINE_RESUME);
    }

    @Override
    public void initTrackerServers(List<TrackerServer> serverList) {
        TrackerServer server = new TrackerServer(new ConnectionlessBootstrap(), getName()) {
            @Override
            protected void addSpecificHandlers(ChannelPipeline pipeline) {
                pipeline.addLast("objectEncoder", new NoranProtocolEncoder());
                pipeline.addLast("objectDecoder", new NoranProtocolDecoder(NoranProtocol.this));
            }
        };
        server.setEndianness(ByteOrder.LITTLE_ENDIAN);
        serverList.add(server);
    }

}
