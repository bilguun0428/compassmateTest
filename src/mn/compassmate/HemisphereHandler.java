package mn.compassmate;

import org.jboss.netty.channel.Channel;
import mn.compassmate.model.Position;

import java.net.SocketAddress;

public class HemisphereHandler extends ExtendedObjectDecoder {

    private int latitudeFactor;
    private int longitudeFactor;

    public HemisphereHandler() {
        String latitudeHemisphere = Context.getConfig().getString("location.latitudeHemisphere");
        if (latitudeHemisphere != null) {
            if (latitudeHemisphere.equalsIgnoreCase("N")) {
                latitudeFactor = 1;
            } else if (latitudeHemisphere.equalsIgnoreCase("S")) {
                latitudeFactor = -1;
            }
        }
        String longitudeHemisphere = Context.getConfig().getString("location.longitudeHemisphere");
        if (longitudeHemisphere != null) {
            if (longitudeHemisphere.equalsIgnoreCase("E")) {
                longitudeFactor = 1;
            } else if (longitudeHemisphere.equalsIgnoreCase("W")) {
                longitudeFactor = -1;
            }
        }
    }

    @Override
    protected Object decode(
            Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {

        if (msg instanceof Position) {
            Position position = (Position) msg;
            if (latitudeFactor != 0) {
                position.setLatitude(Math.abs(position.getLatitude()) * latitudeFactor);
            }
            if (longitudeFactor != 0) {
                position.setLongitude(Math.abs(position.getLongitude()) * longitudeFactor);
            }
        }

        return msg;
    }

}
