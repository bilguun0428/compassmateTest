package mn.compassmate;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.DownstreamMessageEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.handler.logging.LoggingHandler;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import mn.compassmate.events.CommandResultEventHandler;
import mn.compassmate.events.DriverEventHandler;
import mn.compassmate.events.FuelDropEventHandler;
import mn.compassmate.events.GeofenceEventHandler;
import mn.compassmate.events.IgnitionEventHandler;
import mn.compassmate.events.MaintenanceEventHandler;
import mn.compassmate.events.MotionEventHandler;
import mn.compassmate.events.OverspeedEventHandler;
import mn.compassmate.events.AlertEventHandler;
import mn.compassmate.helper.Log;
import mn.compassmate.processing.ComputedAttributesHandler;
import mn.compassmate.processing.CopyAttributesHandler;

import java.net.InetSocketAddress;
import mn.compassmate.events.FuelChargeEventHandler;

public abstract class BasePipelineFactory implements ChannelPipelineFactory {

    private final TrackerServer server;
    private int timeout;

    private FilterHandler filterHandler;
    private DistanceHandler distanceHandler;
    private FuelChargeAmountHandler fuelChargeAmountHandler;
    private RemoteAddressHandler remoteAddressHandler;
    private MotionHandler motionHandler;
    private GeocoderHandler geocoderHandler;
    private GeolocationHandler geolocationHandler;
    private HemisphereHandler hemisphereHandler;
    private CopyAttributesHandler copyAttributesHandler;
    private ComputedAttributesHandler computedAttributesHandler;

    private CommandResultEventHandler commandResultEventHandler;
    private OverspeedEventHandler overspeedEventHandler;
    private FuelDropEventHandler fuelDropEventHandler;
    private FuelChargeEventHandler fuelChargeEventHandler;
    private MotionEventHandler motionEventHandler;
    private GeofenceEventHandler geofenceEventHandler;
    private AlertEventHandler alertEventHandler;
    private IgnitionEventHandler ignitionEventHandler;
    private MaintenanceEventHandler maintenanceEventHandler;
    private DriverEventHandler driverEventHandler;

    private static final class OpenChannelHandler extends SimpleChannelHandler {

        private final TrackerServer server;

        private OpenChannelHandler(TrackerServer server) {
            this.server = server;
        }

        @Override
        public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) {
            server.getChannelGroup().add(e.getChannel());
        }
    }

    private static class StandardLoggingHandler extends LoggingHandler {

        @Override
        public void log(ChannelEvent e) {
            if (e instanceof MessageEvent) {
                MessageEvent event = (MessageEvent) e;
                StringBuilder msg = new StringBuilder();

                msg.append("[").append(String.format("%08X", e.getChannel().getId())).append(": ");
                msg.append(((InetSocketAddress) e.getChannel().getLocalAddress()).getPort());
                if (e instanceof DownstreamMessageEvent) {
                    msg.append(" > ");
                } else {
                    msg.append(" < ");
                }

                if (event.getRemoteAddress() != null) {
                    msg.append(((InetSocketAddress) event.getRemoteAddress()).getHostString());
                } else {
                    msg.append("null");
                }
                msg.append("]");

                if (event.getMessage() instanceof ChannelBuffer) {
                    msg.append(" HEX: ");
                    msg.append(ChannelBuffers.hexDump((ChannelBuffer) event.getMessage()));
                }

                Log.debug(msg.toString());
            }
        }

    }

    public BasePipelineFactory(TrackerServer server, String protocol) {
        this.server = server;

        timeout = Context.getConfig().getInteger(protocol + ".timeout");
        if (timeout == 0) {
            timeout = Context.getConfig().getInteger(protocol + ".resetDelay"); // temporary
            if (timeout == 0) {
                timeout = Context.getConfig().getInteger("server.timeout");
            }
        }

        distanceHandler = new DistanceHandler(
                Context.getConfig().getBoolean("coordinates.filter"),
                Context.getConfig().getInteger("coordinates.minError"),
                Context.getConfig().getInteger("coordinates.maxError"));

        fuelChargeAmountHandler = new FuelChargeAmountHandler();

        if (Context.getConfig().getBoolean("processing.remoteAddress.enable")) {
            remoteAddressHandler = new RemoteAddressHandler();
        }

        if (Context.getConfig().getBoolean("filter.enable")) {
            filterHandler = new FilterHandler();
        }

        if (Context.getGeocoder() != null) {
            geocoderHandler = new GeocoderHandler(
                    Context.getGeocoder(),
                    Context.getConfig().getBoolean("geocoder.processInvalidPositions"));
        }

        if (Context.getGeolocationProvider() != null) {
            geolocationHandler = new GeolocationHandler(
                    Context.getGeolocationProvider(),
                    Context.getConfig().getBoolean("geolocation.processInvalidPositions"));
        }

        motionHandler = new MotionHandler(Context.getConfig().getDouble("event.motion.speedThreshold", 0.01));

        if (Context.getConfig().hasKey("location.latitudeHemisphere")
                || Context.getConfig().hasKey("location.longitudeHemisphere")) {
            hemisphereHandler = new HemisphereHandler();
        }

        if (Context.getConfig().getBoolean("processing.copyAttributes.enable")) {
            copyAttributesHandler = new CopyAttributesHandler();
        }

        if (Context.getConfig().getBoolean("processing.computedAttributes.enable")) {
            computedAttributesHandler = new ComputedAttributesHandler();
        }

        if (Context.getConfig().getBoolean("event.enable")) {
            commandResultEventHandler = new CommandResultEventHandler();
            overspeedEventHandler = Context.getOverspeedEventHandler();
            fuelDropEventHandler = new FuelDropEventHandler();
            fuelChargeEventHandler = new FuelChargeEventHandler();
            motionEventHandler = Context.getMotionEventHandler();
            geofenceEventHandler = new GeofenceEventHandler();
            alertEventHandler = new AlertEventHandler();
            ignitionEventHandler = new IgnitionEventHandler();
            maintenanceEventHandler = new MaintenanceEventHandler();
            driverEventHandler = new DriverEventHandler();
        }
    }

    protected abstract void addSpecificHandlers(ChannelPipeline pipeline);

    @Override
    public ChannelPipeline getPipeline() {
        ChannelPipeline pipeline = Channels.pipeline();
        if (timeout > 0 && !server.isConnectionless()) {
            pipeline.addLast("idleHandler", new IdleStateHandler(GlobalTimer.getTimer(), timeout, 0, 0));
        }
        pipeline.addLast("openHandler", new OpenChannelHandler(server));
        if (Context.isLoggerEnabled()) {
            pipeline.addLast("logger", new StandardLoggingHandler());
        }

        addSpecificHandlers(pipeline);

        if (geolocationHandler != null) {
            pipeline.addLast("location", geolocationHandler);
        }
        if (hemisphereHandler != null) {
            pipeline.addLast("hemisphere", hemisphereHandler);
        }

        if (distanceHandler != null) {
            pipeline.addLast("distance", distanceHandler);
        }

        if (remoteAddressHandler != null) {
            pipeline.addLast("remoteAddress", remoteAddressHandler);
        }

        addDynamicHandlers(pipeline);

        if (filterHandler != null) {
            pipeline.addLast("filter", filterHandler);
        }

        if (geocoderHandler != null) {
            pipeline.addLast("geocoder", geocoderHandler);
        }

        if (motionHandler != null) {
            pipeline.addLast("motion", motionHandler);
        }

        if (copyAttributesHandler != null) {
            pipeline.addLast("copyAttributes", copyAttributesHandler);
        }

        if (computedAttributesHandler != null) {
            pipeline.addLast("computedAttributes", computedAttributesHandler);
        }

        if (fuelChargeAmountHandler != null) {
            pipeline.addLast("fuelChargeing", fuelChargeAmountHandler);
        }

        if (Context.getDataManager() != null) {
            pipeline.addLast("dataHandler", new DefaultDataHandler());
        }

        if (Context.getConfig().getBoolean("forward.enable")) {
            pipeline.addLast("webHandler", new WebDataHandler(Context.getConfig().getString("forward.url")));
        }

        if (commandResultEventHandler != null) {
            pipeline.addLast("CommandResultEventHandler", commandResultEventHandler);
        }

        if (overspeedEventHandler != null) {
            pipeline.addLast("OverspeedEventHandler", overspeedEventHandler);
        }

        if (fuelDropEventHandler != null) {
            pipeline.addLast("FuelDropEventHandler", fuelDropEventHandler);
        }

        if (fuelChargeEventHandler != null) {
            pipeline.addLast("FuelChargeEventHandler", fuelChargeEventHandler);
        }

        if (motionEventHandler != null) {
            pipeline.addLast("MotionEventHandler", motionEventHandler);
        }

        if (geofenceEventHandler != null) {
            pipeline.addLast("GeofenceEventHandler", geofenceEventHandler);
        }

        if (alertEventHandler != null) {
            pipeline.addLast("AlertEventHandler", alertEventHandler);
        }

        if (ignitionEventHandler != null) {
            pipeline.addLast("IgnitionEventHandler", ignitionEventHandler);
        }

        if (maintenanceEventHandler != null) {
            pipeline.addLast("MaintenanceEventHandler", maintenanceEventHandler);
        }

        if (driverEventHandler != null) {
            pipeline.addLast("DriverEventHandler", driverEventHandler);
        }

        pipeline.addLast("mainHandler", new MainEventHandler());
        return pipeline;
    }

    private void addDynamicHandlers(ChannelPipeline pipeline) {
        if (Context.getConfig().hasKey("extra.handlers")) {
            String[] handlers = Context.getConfig().getString("extra.handlers").split(",");
            for (int i = 0; i < handlers.length; i++) {
                try {
                    pipeline.addLast("extraHandler." + i, (ChannelHandler) Class.forName(handlers[i]).newInstance());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException error) {
                    Log.warning(error);
                }
            }
        }
    }
}
