package mn.compassmate.protocol;

import org.jboss.netty.bootstrap.ConnectionlessBootstrap;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import mn.compassmate.BaseProtocol;
import mn.compassmate.TrackerServer;
import mn.compassmate.model.Command;

import java.util.List;

public class AtrackProtocol extends BaseProtocol {

    public AtrackProtocol() {
        super("atrack");
        setSupportedDataCommands(
                Command.TYPE_CUSTOM);
    }

    @Override
    public void initTrackerServers(List<TrackerServer> serverList) {
        serverList.add(new TrackerServer(new ServerBootstrap(), getName()) {
            @Override
            protected void addSpecificHandlers(ChannelPipeline pipeline) {
                pipeline.addLast("frameDecoder", new AtrackFrameDecoder());
                pipeline.addLast("objectEncoder", new AtrackProtocolEncoder());
                pipeline.addLast("objectDecoder", new AtrackProtocolDecoder(AtrackProtocol.this));
            }
        });
        serverList.add(new TrackerServer(new ConnectionlessBootstrap(), getName()) {
            @Override
            protected void addSpecificHandlers(ChannelPipeline pipeline) {
                pipeline.addLast("objectEncoder", new AtrackProtocolEncoder());
                pipeline.addLast("objectDecoder", new AtrackProtocolDecoder(AtrackProtocol.this));
            }
        });
    }

}
