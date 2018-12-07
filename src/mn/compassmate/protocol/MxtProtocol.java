package mn.compassmate.protocol;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import mn.compassmate.BaseProtocol;
import mn.compassmate.TrackerServer;

import java.nio.ByteOrder;
import java.util.List;

public class MxtProtocol extends BaseProtocol {

    public MxtProtocol() {
        super("mxt");
    }

    @Override
    public void initTrackerServers(List<TrackerServer> serverList) {
        TrackerServer server = new TrackerServer(new ServerBootstrap(), getName()) {
            @Override
            protected void addSpecificHandlers(ChannelPipeline pipeline) {
                    pipeline.addLast("frameDecoder", new MxtFrameDecoder());
                    pipeline.addLast("objectDecoder", new MxtProtocolDecoder(MxtProtocol.this));
                }
        };
        server.setEndianness(ByteOrder.LITTLE_ENDIAN);
        serverList.add(server);
    }

}
