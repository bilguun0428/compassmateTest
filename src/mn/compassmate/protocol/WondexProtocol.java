package mn.compassmate.protocol;

import org.jboss.netty.bootstrap.ConnectionlessBootstrap;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.handler.codec.string.StringEncoder;
import mn.compassmate.BaseProtocol;
import mn.compassmate.TrackerServer;
import mn.compassmate.model.Command;

import java.util.List;

public class WondexProtocol extends BaseProtocol {

    public WondexProtocol() {
        super("wondex");
        setTextCommandEncoder(new WondexProtocolEncoder());
        setSupportedCommands(
                Command.TYPE_GET_DEVICE_STATUS,
                Command.TYPE_GET_MODEM_STATUS,
                Command.TYPE_REBOOT_DEVICE,
                Command.TYPE_POSITION_SINGLE,
                Command.TYPE_GET_VERSION,
                Command.TYPE_IDENTIFICATION);
    }

    @Override
    public void initTrackerServers(List<TrackerServer> serverList) {
        serverList.add(new TrackerServer(new ServerBootstrap(), getName()) {
            @Override
            protected void addSpecificHandlers(ChannelPipeline pipeline) {
                pipeline.addLast("frameDecoder", new WondexFrameDecoder());
                pipeline.addLast("stringEncoder", new StringEncoder());
                pipeline.addLast("objectEncoder", new WondexProtocolEncoder());
                pipeline.addLast("objectDecoder", new WondexProtocolDecoder(WondexProtocol.this));
            }
        });
        serverList.add(new TrackerServer(new ConnectionlessBootstrap(), getName()) {
            @Override
            protected void addSpecificHandlers(ChannelPipeline pipeline) {
                pipeline.addLast("stringEncoder", new StringEncoder());
                pipeline.addLast("objectEncoder", new WondexProtocolEncoder());
                pipeline.addLast("objectDecoder", new WondexProtocolDecoder(WondexProtocol.this));
            }
        });
    }

}
