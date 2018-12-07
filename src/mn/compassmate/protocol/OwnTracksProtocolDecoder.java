package mn.compassmate.protocol;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import mn.compassmate.BaseProtocolDecoder;
import mn.compassmate.DeviceSession;
import mn.compassmate.model.Position;
import mn.compassmate.helper.UnitsConverter;

import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import java.io.StringReader;
import javax.json.Json;
import javax.json.JsonObject;

public class OwnTracksProtocolDecoder extends BaseProtocolDecoder {

    public OwnTracksProtocolDecoder(OwnTracksProtocol protocol) {
        super(protocol);
    }

    private void sendResponse(Channel channel, HttpResponseStatus status) {
        if (channel != null) {
            HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, status);
            response.headers().add(HttpHeaders.Names.CONTENT_LENGTH, 0);
            channel.write(response);
        }
    }

    @Override
    protected Object decode(
            Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {

        HttpRequest request = (HttpRequest) msg;
        JsonObject root = Json.createReader(
                new StringReader(request.getContent().toString(StandardCharsets.US_ASCII))).readObject();

        if (!root.containsKey("_type") || !root.getString("_type").equals("location")) {
            sendResponse(channel, HttpResponseStatus.OK);
            return null;
        }

        Position position = new Position();
        position.setProtocol(getProtocolName());
        position.setValid(true);

        position.setLatitude(root.getJsonNumber("lat").doubleValue());
        position.setLongitude(root.getJsonNumber("lon").doubleValue());

        if (root.containsKey("vel")) {
            position.setSpeed(UnitsConverter.knotsFromKph(root.getInt("vel")));
        }
        if (root.containsKey("alt")) {
            position.setAltitude(root.getInt("alt"));
        }
        if (root.containsKey("cog")) {
            position.setCourse(root.getInt("cog"));
        }
        if (root.containsKey("acc")) {
            position.setAccuracy(root.getInt("acc"));
        }
        if (root.containsKey("t")) {
            position.set("t", root.getString("t"));
        }
        if (root.containsKey("batt")) {
            position.set(Position.KEY_BATTERY, root.getInt("batt"));
        }

        position.setTime(new Date(root.getJsonNumber("tst").longValue() * 1000));

        String uniqueId;

        if (root.containsKey("topic")) {
            uniqueId = root.getString("topic");
            if (root.containsKey("tid")) {
                position.set("tid", root.getString("tid"));
            }
        } else {
            uniqueId = root.getString("tid");
        }

        DeviceSession deviceSession = getDeviceSession(channel, remoteAddress, uniqueId);
        if (deviceSession == null) {
            sendResponse(channel, HttpResponseStatus.BAD_REQUEST);
            return null;
        }
        position.setDeviceId(deviceSession.getDeviceId());

        sendResponse(channel, HttpResponseStatus.OK);
        return position;
    }
}
