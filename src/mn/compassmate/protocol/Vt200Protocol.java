package mn.compassmate.protocol;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import mn.compassmate.BaseProtocol;
import mn.compassmate.TrackerServer;

import java.util.List;

public class Vt200Protocol extends BaseProtocol {

    public Vt200Protocol() {
        super("vt200");
    }

    @Override
    public void initTrackerServers(List<TrackerServer> serverList) {
        serverList.add(new TrackerServer(new ServerBootstrap(), getName()) {
            @Override
            protected void addSpecificHandlers(ChannelPipeline pipeline) {
                pipeline.addLast("frameDecoder", new Vt200FrameDecoder());
                pipeline.addLast("objectDecoder", new Vt200ProtocolDecoder(Vt200Protocol.this));
            }
        });
    }

}
