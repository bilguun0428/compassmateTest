package mn.compassmate.protocol;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import mn.compassmate.BaseProtocol;
import mn.compassmate.TrackerServer;

import java.util.List;

public class ThinkRaceProtocol extends BaseProtocol {

    public ThinkRaceProtocol() {
        super("thinkrace");
    }

    @Override
    public void initTrackerServers(List<TrackerServer> serverList) {
        serverList.add(new TrackerServer(new ServerBootstrap(), getName()) {
            @Override
            protected void addSpecificHandlers(ChannelPipeline pipeline) {
                pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(1024, 2 + 12 + 1 + 1, 2, 2, 0));
                pipeline.addLast("objectDecoder", new ThinkRaceProtocolDecoder(ThinkRaceProtocol.this));
            }
        });
    }

}
