package mn.compassmate.protocol;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;
import mn.compassmate.BaseProtocol;
import mn.compassmate.TrackerServer;
import mn.compassmate.model.Command;

import java.nio.ByteOrder;
import java.util.List;

public class Pt502Protocol extends BaseProtocol {

    public Pt502Protocol() {
        super("pt502");
        setSupportedDataCommands(
                Command.TYPE_SET_TIMEZONE,
                Command.TYPE_ALARM_SPEED,
                Command.TYPE_OUTPUT_CONTROL,
                Command.TYPE_REQUEST_PHOTO);
    }

    @Override
    public void initTrackerServers(List<TrackerServer> serverList) {
        TrackerServer server = new TrackerServer(new ServerBootstrap(), getName()) {
            @Override
            protected void addSpecificHandlers(ChannelPipeline pipeline) {
                pipeline.addLast("frameDecoder", new Pt502FrameDecoder());
                pipeline.addLast("stringEncoder", new StringEncoder());
                pipeline.addLast("stringDecoder", new StringDecoder());
                pipeline.addLast("objectEncoder", new Pt502ProtocolEncoder());
                pipeline.addLast("objectDecoder", new Pt502ProtocolDecoder(Pt502Protocol.this));
            }
        };
        server.setEndianness(ByteOrder.LITTLE_ENDIAN);
        serverList.add(server);
    }

}
