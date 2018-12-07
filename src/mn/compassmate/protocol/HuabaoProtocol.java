package mn.compassmate.protocol;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import mn.compassmate.BaseProtocol;
import mn.compassmate.TrackerServer;
import mn.compassmate.model.Command;

import java.util.List;

public class HuabaoProtocol extends BaseProtocol {

    public HuabaoProtocol() {
        super("huabao");
        setSupportedDataCommands(
                Command.TYPE_ENGINE_STOP,
                Command.TYPE_ENGINE_RESUME);
    }

    @Override
    public void initTrackerServers(List<TrackerServer> serverList) {
        serverList.add(new TrackerServer(new ServerBootstrap(), getName()) {
            @Override
            protected void addSpecificHandlers(ChannelPipeline pipeline) {
                pipeline.addLast("frameDecoder", new HuabaoFrameDecoder());
                pipeline.addLast("objectEncoder", new HuabaoProtocolEncoder());
                pipeline.addLast("objectDecoder", new HuabaoProtocolDecoder(HuabaoProtocol.this));
            }
        });
    }

}
