package mn.compassmate.protocol;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.handler.codec.frame.LineBasedFrameDecoder;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;
import mn.compassmate.BaseProtocol;
import mn.compassmate.Context;
import mn.compassmate.TrackerServer;
import mn.compassmate.model.Command;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class WialonProtocol extends BaseProtocol {

    public WialonProtocol() {
        super("wialon");
        setSupportedDataCommands(
                Command.TYPE_REBOOT_DEVICE,
                Command.TYPE_SEND_USSD,
                Command.TYPE_IDENTIFICATION,
                Command.TYPE_OUTPUT_CONTROL);
    }

    @Override
    public void initTrackerServers(List<TrackerServer> serverList) {
        serverList.add(new TrackerServer(new ServerBootstrap(), getName()) {
            @Override
            protected void addSpecificHandlers(ChannelPipeline pipeline) {
                pipeline.addLast("frameDecoder", new LineBasedFrameDecoder(4 * 1024));
                pipeline.addLast("stringEncoder", new StringEncoder());
                boolean utf8 = Context.getConfig().getBoolean(getName() + ".utf8");
                if (utf8) {
                    pipeline.addLast("stringDecoder", new StringDecoder(StandardCharsets.UTF_8));
                } else {
                    pipeline.addLast("stringDecoder", new StringDecoder());
                }
                pipeline.addLast("objectEncoder", new WialonProtocolEncoder());
                pipeline.addLast("objectDecoder", new WialonProtocolDecoder(WialonProtocol.this));
            }
        });
    }

}
