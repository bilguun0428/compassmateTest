package mn.compassmate.protocol;

import org.jboss.netty.bootstrap.ConnectionlessBootstrap;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import mn.compassmate.BaseProtocol;
import mn.compassmate.TrackerServer;
import mn.compassmate.model.Command;

import java.util.List;

public class TeltonikaProtocol extends BaseProtocol {

    public TeltonikaProtocol() {
        super("teltonika");
        setSupportedDataCommands(
                Command.TYPE_CUSTOM);
    }

    @Override
    public void initTrackerServers(List<TrackerServer> serverList) {
        serverList.add(new TrackerServer(new ServerBootstrap(), getName()) {
            @Override
            protected void addSpecificHandlers(ChannelPipeline pipeline) {
                pipeline.addLast("frameDecoder", new TeltonikaFrameDecoder());
                pipeline.addLast("objectEncoder", new TeltonikaProtocolEncoder());
                pipeline.addLast("objectDecoder", new TeltonikaProtocolDecoder(TeltonikaProtocol.this, false));
            }
        });
        serverList.add(new TrackerServer(new ConnectionlessBootstrap(), getName()) {
            @Override
            protected void addSpecificHandlers(ChannelPipeline pipeline) {
                pipeline.addLast("objectEncoder", new TeltonikaProtocolEncoder());
                pipeline.addLast("objectDecoder", new TeltonikaProtocolDecoder(TeltonikaProtocol.this, true));
            }
        });
    }

}
