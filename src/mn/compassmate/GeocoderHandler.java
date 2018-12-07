package mn.compassmate;

import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import mn.compassmate.geocoder.AddressFormat;
import mn.compassmate.geocoder.Geocoder;
import mn.compassmate.helper.Log;
import mn.compassmate.model.Position;

public class GeocoderHandler implements ChannelUpstreamHandler {

    private final Geocoder geocoder;
    private final boolean processInvalidPositions;
    private final AddressFormat addressFormat;
    private final int geocoderReuseDistance;

    public GeocoderHandler(Geocoder geocoder, boolean processInvalidPositions) {
        this.geocoder = geocoder;
        this.processInvalidPositions = processInvalidPositions;

        String formatString = Context.getConfig().getString("geocoder.format");
        if (formatString != null) {
            addressFormat = new AddressFormat(formatString);
        } else {
            addressFormat = new AddressFormat();
        }

        geocoderReuseDistance = Context.getConfig().getInteger("geocoder.reuseDistance", 0);
    }

    @Override
    public void handleUpstream(final ChannelHandlerContext ctx, ChannelEvent evt) throws Exception {
        if (!(evt instanceof MessageEvent)) {
            ctx.sendUpstream(evt);
            return;
        }

        final MessageEvent event = (MessageEvent) evt;
        Object message = event.getMessage();
        if (message instanceof Position) {
            final Position position = (Position) message;
            if (processInvalidPositions || position.getValid()) {
                if (geocoderReuseDistance != 0) {
                    Position lastPosition = Context.getIdentityManager().getLastPosition(position.getDeviceId());
                    if (lastPosition != null && lastPosition.getAddress() != null
                            && position.getDouble(Position.KEY_DISTANCE) <= geocoderReuseDistance) {
                        position.setAddress(lastPosition.getAddress());
                        Channels.fireMessageReceived(ctx, position, event.getRemoteAddress());
                        return;
                    }
                }

                Context.getStatisticsManager().registerGeocoderRequest();

                geocoder.getAddress(addressFormat, position.getLatitude(), position.getLongitude(),
                        new Geocoder.ReverseGeocoderCallback() {
                    @Override
                    public void onSuccess(String address) {
                        position.setAddress(address);
                        Channels.fireMessageReceived(ctx, position, event.getRemoteAddress());
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        Log.warning("Geocoding failed", e);
                        Channels.fireMessageReceived(ctx, position, event.getRemoteAddress());
                    }
                });
            } else {
                Channels.fireMessageReceived(ctx, position, event.getRemoteAddress());
            }
        } else {
            Channels.fireMessageReceived(ctx, message, event.getRemoteAddress());
        }
    }

}
