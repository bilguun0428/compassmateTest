package mn.compassmate.protocol;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import mn.compassmate.BaseProtocol;
import mn.compassmate.TrackerServer;
import mn.compassmate.model.Command;

import java.nio.ByteOrder;
import java.util.List;

public class GranitProtocol extends BaseProtocol {

    public GranitProtocol() {
        super("granit");
        setSupportedDataCommands(
                Command.TYPE_IDENTIFICATION,
                Command.TYPE_REBOOT_DEVICE,
                Command.TYPE_POSITION_SINGLE);
        setTextCommandEncoder(new GranitProtocolSmsEncoder());
        setSupportedTextCommands(
                Command.TYPE_REBOOT_DEVICE,
                Command.TYPE_POSITION_PERIODIC);
    }

    @Override
    public void initTrackerServers(List<TrackerServer> serverList) {
        TrackerServer server = new TrackerServer(new ServerBootstrap(), getName()) {
            @Override
            protected void addSpecificHandlers(ChannelPipeline pipeline) {
                pipeline.addLast("frameDecoder", new GranitFrameDecoder());
                pipeline.addLast("objectEncoder", new GranitProtocolEncoder());
                pipeline.addLast("objectDecoder", new GranitProtocolDecoder(GranitProtocol.this));
            }
        };
        server.setEndianness(ByteOrder.LITTLE_ENDIAN);
        serverList.add(server);
    }

}
