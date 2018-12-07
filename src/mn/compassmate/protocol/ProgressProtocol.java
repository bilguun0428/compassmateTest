package mn.compassmate.protocol;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import mn.compassmate.BaseProtocol;
import mn.compassmate.TrackerServer;

import java.nio.ByteOrder;
import java.util.List;

public class ProgressProtocol extends BaseProtocol {

    public ProgressProtocol() {
        super("progress");
    }

    @Override
    public void initTrackerServers(List<TrackerServer> serverList) {
        TrackerServer server = new TrackerServer(new ServerBootstrap(), getName()) {
            @Override
            protected void addSpecificHandlers(ChannelPipeline pipeline) {
                pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(1024, 2, 2, 4, 0));
                pipeline.addLast("objectDecoder", new ProgressProtocolDecoder(ProgressProtocol.this));
            }
        };
        server.setEndianness(ByteOrder.LITTLE_ENDIAN);
        serverList.add(server);
    }

}
