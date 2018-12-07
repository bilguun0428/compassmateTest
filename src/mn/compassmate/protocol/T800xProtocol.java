package mn.compassmate.protocol;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import mn.compassmate.BaseProtocol;
import mn.compassmate.TrackerServer;
import mn.compassmate.model.Command;

import java.util.List;

public class T800xProtocol extends BaseProtocol {

    public T800xProtocol() {
        super("t800x");
        setSupportedDataCommands(
                Command.TYPE_CUSTOM);
    }

    @Override
    public void initTrackerServers(List<TrackerServer> serverList) {
        serverList.add(new TrackerServer(new ServerBootstrap(), getName()) {
            @Override
            protected void addSpecificHandlers(ChannelPipeline pipeline) {
                pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(1024, 3, 2, -5, 0));
                pipeline.addLast("objectEncoder", new T800xProtocolEncoder());
                pipeline.addLast("objectDecoder", new T800xProtocolDecoder(T800xProtocol.this));
            }
        });
    }

}
