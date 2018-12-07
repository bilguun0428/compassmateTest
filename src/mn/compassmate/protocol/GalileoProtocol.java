package mn.compassmate.protocol;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import mn.compassmate.BaseProtocol;
import mn.compassmate.TrackerServer;
import mn.compassmate.model.Command;

import java.nio.ByteOrder;
import java.util.List;

public class GalileoProtocol extends BaseProtocol {

    public GalileoProtocol() {
        super("galileo");
        setSupportedDataCommands(
                Command.TYPE_CUSTOM,
                Command.TYPE_OUTPUT_CONTROL);
    }

    @Override
    public void initTrackerServers(List<TrackerServer> serverList) {
        TrackerServer server = new TrackerServer(new ServerBootstrap(), getName()) {
            @Override
            protected void addSpecificHandlers(ChannelPipeline pipeline) {
                pipeline.addLast("frameDecoder", new GalileoFrameDecoder());
                pipeline.addLast("objectEncoder", new GalileoProtocolEncoder());
                pipeline.addLast("objectDecoder", new GalileoProtocolDecoder(GalileoProtocol.this));
            }
        };
        server.setEndianness(ByteOrder.LITTLE_ENDIAN);
        serverList.add(server);
    }

}
