package mn.compassmate.protocol;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import mn.compassmate.BaseProtocol;
import mn.compassmate.TrackerServer;

import java.util.List;

public class NvsProtocol extends BaseProtocol {

    public NvsProtocol() {
        super("nvs");
    }

    @Override
    public void initTrackerServers(List<TrackerServer> serverList) {
        serverList.add(new TrackerServer(new ServerBootstrap(), getName()) {
            @Override
            protected void addSpecificHandlers(ChannelPipeline pipeline) {
                pipeline.addLast("frameDecoder", new NvsFrameDecoder());
                pipeline.addLast("objectDecoder", new NvsProtocolDecoder(NvsProtocol.this));
            }
        });
    }

}
