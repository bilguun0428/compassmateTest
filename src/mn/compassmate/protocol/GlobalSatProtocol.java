package mn.compassmate.protocol;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;
import mn.compassmate.BaseProtocol;
import mn.compassmate.CharacterDelimiterFrameDecoder;
import mn.compassmate.TrackerServer;

import java.util.List;

public class GlobalSatProtocol extends BaseProtocol {

    public GlobalSatProtocol() {
        super("globalsat");
    }

    @Override
    public void initTrackerServers(List<TrackerServer> serverList) {
        serverList.add(new TrackerServer(new ServerBootstrap(), getName()) {
            @Override
            protected void addSpecificHandlers(ChannelPipeline pipeline) {
                pipeline.addLast("frameDecoder", new CharacterDelimiterFrameDecoder(1024, '!'));
                pipeline.addLast("stringEncoder", new StringEncoder());
                pipeline.addLast("stringDecoder", new StringDecoder());
                pipeline.addLast("objectDecoder", new GlobalSatProtocolDecoder(GlobalSatProtocol.this));
            }
        });
    }

}
