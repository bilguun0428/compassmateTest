package mn.compassmate.protocol;

import org.jboss.netty.bootstrap.ConnectionlessBootstrap;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.handler.codec.string.StringEncoder;
import mn.compassmate.BaseProtocol;
import mn.compassmate.Context;
import mn.compassmate.TrackerServer;
import mn.compassmate.model.Command;

import java.util.List;

public class H02Protocol extends BaseProtocol {

    public H02Protocol() {
        super("h02");
        setSupportedDataCommands(
                Command.TYPE_ALARM_ARM,
                Command.TYPE_ALARM_DISARM,
                Command.TYPE_ENGINE_STOP,
                Command.TYPE_ENGINE_RESUME,
                Command.TYPE_POSITION_PERIODIC
        );
    }

    @Override
    public void initTrackerServers(List<TrackerServer> serverList) {
        serverList.add(new TrackerServer(new ServerBootstrap(), getName()) {
            @Override
            protected void addSpecificHandlers(ChannelPipeline pipeline) {
                int messageLength = Context.getConfig().getInteger(getName() + ".messageLength");
                pipeline.addLast("frameDecoder", new H02FrameDecoder(messageLength));
                pipeline.addLast("stringEncoder", new StringEncoder());
                pipeline.addLast("objectEncoder", new H02ProtocolEncoder());
                pipeline.addLast("objectDecoder", new H02ProtocolDecoder(H02Protocol.this));
            }
        });
        serverList.add(new TrackerServer(new ConnectionlessBootstrap(), getName()) {
            @Override
            protected void addSpecificHandlers(ChannelPipeline pipeline) {
                pipeline.addLast("stringEncoder", new StringEncoder());
                pipeline.addLast("objectEncoder", new H02ProtocolEncoder());
                pipeline.addLast("objectDecoder", new H02ProtocolDecoder(H02Protocol.this));
            }
        });
    }
}
