package mn.compassmate.protocol;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.handler.codec.string.StringEncoder;
import mn.compassmate.BaseProtocol;
import mn.compassmate.TrackerServer;
import mn.compassmate.model.Command;

import java.util.List;

public class WatchProtocol extends BaseProtocol {

    public WatchProtocol() {
        super("watch");
        setSupportedDataCommands(
                Command.TYPE_POSITION_SINGLE,
                Command.TYPE_POSITION_PERIODIC,
                Command.TYPE_SOS_NUMBER,
                Command.TYPE_ALARM_SOS,
                Command.TYPE_ALARM_BATTERY,
                Command.TYPE_REBOOT_DEVICE,
                Command.TYPE_ALARM_REMOVE,
                Command.TYPE_SILENCE_TIME,
                Command.TYPE_ALARM_CLOCK,
                Command.TYPE_SET_PHONEBOOK,
                Command.TYPE_VOICE_MESSAGE,
                Command.TYPE_SET_TIMEZONE,
                Command.TYPE_SET_INDICATOR);
    }

    @Override
    public void initTrackerServers(List<TrackerServer> serverList) {
        serverList.add(new TrackerServer(new ServerBootstrap(), getName()) {
            @Override
            protected void addSpecificHandlers(ChannelPipeline pipeline) {
                pipeline.addLast("frameDecoder", new WatchFrameDecoder());
                pipeline.addLast("stringEncoder", new StringEncoder());
                pipeline.addLast("objectEncoder", new WatchProtocolEncoder());
                pipeline.addLast("objectDecoder", new WatchProtocolDecoder(WatchProtocol.this));
            }
        });
    }

}
