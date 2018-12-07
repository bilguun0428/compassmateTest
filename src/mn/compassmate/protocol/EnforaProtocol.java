package mn.compassmate.protocol;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import mn.compassmate.BaseProtocol;
import mn.compassmate.TrackerServer;
import mn.compassmate.model.Command;

import java.util.List;

public class EnforaProtocol extends BaseProtocol {

    public EnforaProtocol() {
        super("enfora");
        setSupportedDataCommands(
                Command.TYPE_CUSTOM,
                Command.TYPE_ENGINE_STOP,
                Command.TYPE_ENGINE_RESUME);
    }

    @Override
    public void initTrackerServers(List<TrackerServer> serverList) {
        serverList.add(new TrackerServer(new ServerBootstrap(), getName()) {
            @Override
            protected void addSpecificHandlers(ChannelPipeline pipeline) {
                pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(1024, 0, 2, -2, 2));
                pipeline.addLast("objectEncoder", new EnforaProtocolEncoder());
                pipeline.addLast("objectDecoder", new EnforaProtocolDecoder(EnforaProtocol.this));
            }
        });
    }

}
