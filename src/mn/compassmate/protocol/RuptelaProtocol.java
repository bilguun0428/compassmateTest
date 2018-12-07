package mn.compassmate.protocol;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import mn.compassmate.BaseProtocol;
import mn.compassmate.TrackerServer;
import mn.compassmate.model.Command;

import java.util.List;

public class RuptelaProtocol extends BaseProtocol {

    public RuptelaProtocol() {
        super("ruptela");
        setSupportedDataCommands(
                Command.TYPE_CUSTOM,
                Command.TYPE_CONFIGURATION,
                Command.TYPE_GET_VERSION,
                Command.TYPE_FIRMWARE_UPDATE,
                Command.TYPE_OUTPUT_CONTROL,
                Command.TYPE_SET_CONNECTION,
                Command.TYPE_SET_ODOMETER);
    }

    @Override
    public void initTrackerServers(List<TrackerServer> serverList) {
        serverList.add(new TrackerServer(new ServerBootstrap(), getName()) {
            @Override
            protected void addSpecificHandlers(ChannelPipeline pipeline) {
                pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(1024, 0, 2, 2, 0));
                pipeline.addLast("objectEncoder", new RuptelaProtocolEncoder());
                pipeline.addLast("objectDecoder", new RuptelaProtocolDecoder(RuptelaProtocol.this));
            }
        });
    }

}
