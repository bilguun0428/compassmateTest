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

import java.util.List;

public class XexunProtocol extends BaseProtocol {

    public XexunProtocol() {
        super("xexun");
        setSupportedDataCommands(
                Command.TYPE_ENGINE_STOP,
                Command.TYPE_ENGINE_RESUME);
    }

    @Override
    public void initTrackerServers(List<TrackerServer> serverList) {
        serverList.add(new TrackerServer(new ServerBootstrap(), getName()) {
            @Override
            protected void addSpecificHandlers(ChannelPipeline pipeline) {
                boolean full = Context.getConfig().getBoolean(getName() + ".extended");
                if (full) {
                    pipeline.addLast("frameDecoder", new LineBasedFrameDecoder(1024)); // tracker bug \n\r
                } else {
                    pipeline.addLast("frameDecoder", new XexunFrameDecoder());
                }
                pipeline.addLast("stringEncoder", new StringEncoder());
                pipeline.addLast("stringDecoder", new StringDecoder());
                pipeline.addLast("objectEncoder", new XexunProtocolEncoder());
                pipeline.addLast("objectDecoder", new XexunProtocolDecoder(XexunProtocol.this, full));
            }
        });
    }

}
