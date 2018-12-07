package mn.compassmate.protocol;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import mn.compassmate.BaseProtocol;
import mn.compassmate.TrackerServer;

import java.util.List;

public class Avl301Protocol extends BaseProtocol {

    public Avl301Protocol() {
        super("avl301");
    }

    @Override
    public void initTrackerServers(List<TrackerServer> serverList) {
        serverList.add(new TrackerServer(new ServerBootstrap(), getName()) {
            @Override
            protected void addSpecificHandlers(ChannelPipeline pipeline) {
                pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(256, 2, 1, -3, 0));
                pipeline.addLast("objectDecoder", new Avl301ProtocolDecoder(Avl301Protocol.this));
            }
        });
    }

}
