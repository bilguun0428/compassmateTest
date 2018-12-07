package mn.compassmate.protocol;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import mn.compassmate.BaseProtocol;
import mn.compassmate.TrackerServer;
import mn.compassmate.model.Command;

import java.util.List;

public class Gt06Protocol extends BaseProtocol {

    public Gt06Protocol() {
        super("gt06");
        setSupportedDataCommands(
                Command.TYPE_ENGINE_STOP,
                Command.TYPE_ENGINE_RESUME,
                Command.TYPE_CUSTOM);
    }

    @Override
    public void initTrackerServers(List<TrackerServer> serverList) {
        serverList.add(new TrackerServer(new ServerBootstrap(), getName()) {
            @Override
            protected void addSpecificHandlers(ChannelPipeline pipeline) {
                pipeline.addLast("frameDecoder", new Gt06FrameDecoder());
                pipeline.addLast("objectEncoder", new Gt06ProtocolEncoder());
                pipeline.addLast("objectDecoder", new Gt06ProtocolDecoder(Gt06Protocol.this));
            }
        });
    }

}
