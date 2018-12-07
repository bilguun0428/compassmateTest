package mn.compassmate.protocol;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.handler.codec.frame.FixedLengthFrameDecoder;
import mn.compassmate.BaseProtocol;
import mn.compassmate.TrackerServer;

import java.util.List;

public class M2mProtocol extends BaseProtocol {

    public M2mProtocol() {
        super("m2m");
    }

    @Override
    public void initTrackerServers(List<TrackerServer> serverList) {
        serverList.add(new TrackerServer(new ServerBootstrap(), getName()) {
            @Override
            protected void addSpecificHandlers(ChannelPipeline pipeline) {
                pipeline.addLast("frameDecoder", new FixedLengthFrameDecoder(23));
                pipeline.addLast("objectDecoder", new M2mProtocolDecoder(M2mProtocol.this));
            }
        });
    }

}
