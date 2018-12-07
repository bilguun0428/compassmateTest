package mn.compassmate.protocol;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import mn.compassmate.BaseProtocol;
import mn.compassmate.Context;
import mn.compassmate.TrackerServer;

import java.util.List;

public class Mta6Protocol extends BaseProtocol {

    public Mta6Protocol() {
        super("mta6");
    }

    @Override
    public void initTrackerServers(List<TrackerServer> serverList) {
        serverList.add(new TrackerServer(new ServerBootstrap(), getName()) {
            @Override
            protected void addSpecificHandlers(ChannelPipeline pipeline) {
                pipeline.addLast("httpEncoder", new HttpResponseEncoder());
                pipeline.addLast("httpDecoder", new HttpRequestDecoder());
                pipeline.addLast("objectDecoder", new Mta6ProtocolDecoder(
                        Mta6Protocol.this, !Context.getConfig().getBoolean(getName() + ".can")));
            }
        });
    }

}
