package mn.compassmate.protocol;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import mn.compassmate.BaseProtocol;
import mn.compassmate.TrackerServer;

import java.util.List;

public class Gps056Protocol extends BaseProtocol {

    public Gps056Protocol() {
        super("gps056");
    }

    @Override
    public void initTrackerServers(List<TrackerServer> serverList) {
        serverList.add(new TrackerServer(new ServerBootstrap(), getName()) {
            @Override
            protected void addSpecificHandlers(ChannelPipeline pipeline) {
                pipeline.addLast("frameDecoder", new Gps056FrameDecoder());
                pipeline.addLast("objectDecoder", new Gps056ProtocolDecoder(Gps056Protocol.this));
            }
        });
    }

}
