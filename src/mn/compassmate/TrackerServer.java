package mn.compassmate;

import org.jboss.netty.bootstrap.Bootstrap;
import org.jboss.netty.bootstrap.ConnectionlessBootstrap;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.HeapChannelBufferFactory;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.DefaultChannelGroup;

import java.net.InetSocketAddress;
import java.nio.ByteOrder;

public abstract class TrackerServer {

    private final Bootstrap bootstrap;
    private final String protocol;

    public boolean isConnectionless() {
        return bootstrap instanceof ConnectionlessBootstrap;
    }

    public String getProtocol() {
        return protocol;
    }

    public TrackerServer(Bootstrap bootstrap, String protocol) {
        this.bootstrap = bootstrap;
        this.protocol = protocol;

        if (bootstrap instanceof ServerBootstrap) {
            bootstrap.setFactory(GlobalChannelFactory.getFactory());
        } else if (bootstrap instanceof ConnectionlessBootstrap) {
            bootstrap.setFactory(GlobalChannelFactory.getDatagramFactory());
        }

        address = Context.getConfig().getString(protocol + ".address");
        port = Context.getConfig().getInteger(protocol + ".port");

        bootstrap.setPipelineFactory(new BasePipelineFactory(this, protocol) {
            @Override
            protected void addSpecificHandlers(ChannelPipeline pipeline) {
                TrackerServer.this.addSpecificHandlers(pipeline);
            }
        });
    }

    protected abstract void addSpecificHandlers(ChannelPipeline pipeline);

    private int port;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    private String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setEndianness(ByteOrder byteOrder) {
        bootstrap.setOption("bufferFactory", new HeapChannelBufferFactory(byteOrder));
        bootstrap.setOption("child.bufferFactory", new HeapChannelBufferFactory(byteOrder));
    }

    private final ChannelGroup allChannels = new DefaultChannelGroup();

    public ChannelGroup getChannelGroup() {
        return allChannels;
    }

    public void setPipelineFactory(ChannelPipelineFactory pipelineFactory) {
        bootstrap.setPipelineFactory(pipelineFactory);
    }

    public ChannelPipelineFactory getPipelineFactory() {
        return bootstrap.getPipelineFactory();
    }

    public void start() {
        InetSocketAddress endpoint;
        if (address == null) {
            endpoint = new InetSocketAddress(port);
        } else {
            endpoint = new InetSocketAddress(address, port);
        }

        Channel channel = null;
        if (bootstrap instanceof ServerBootstrap) {
            channel = ((ServerBootstrap) bootstrap).bind(endpoint);
        } else if (bootstrap instanceof ConnectionlessBootstrap) {
            channel = ((ConnectionlessBootstrap) bootstrap).bind(endpoint);
        }

        if (channel != null) {
            getChannelGroup().add(channel);
        }
    }

    public void stop() {
        ChannelGroupFuture future = getChannelGroup().close();
        future.awaitUninterruptibly();
    }

}
