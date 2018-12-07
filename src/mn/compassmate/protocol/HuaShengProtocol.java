package mn.compassmate.protocol;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import mn.compassmate.BaseProtocol;
import mn.compassmate.TrackerServer;

import java.util.List;

public class HuaShengProtocol extends BaseProtocol {

    public HuaShengProtocol() {
        super("huasheng");
    }

    @Override
    public void initTrackerServers(List<TrackerServer> serverList) {
        serverList.add(new TrackerServer(new ServerBootstrap(), getName()) {
            @Override
            protected void addSpecificHandlers(ChannelPipeline pipeline) {
                pipeline.addLast("frameDecoder", new HuaShengFrameDecoder());
                pipeline.addLast("objectDecoder", new HuaShengProtocolDecoder(HuaShengProtocol.this));
            }
        });
    }

}
