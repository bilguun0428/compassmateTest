package mn.compassmate.notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ning.http.client.AsyncHttpClient.BoundRequestBuilder;
import mn.compassmate.Context;
import mn.compassmate.helper.Log;
import mn.compassmate.model.Device;
import mn.compassmate.model.Event;
import mn.compassmate.model.Geofence;
import mn.compassmate.model.Position;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public final class EventForwarder {

    private String url;
    private String header;

    public EventForwarder() {
        url = Context.getConfig().getString("event.forward.url", "http://localhost/");
        header = Context.getConfig().getString("event.forward.header", "");
    }

    private static final String KEY_POSITION = "position";
    private static final String KEY_EVENT = "event";
    private static final String KEY_GEOFENCE = "geofence";
    private static final String KEY_DEVICE = "device";

    public void forwardEvent(Event event, Position position) {

        BoundRequestBuilder requestBuilder = Context.getAsyncHttpClient().preparePost(url);

        requestBuilder.addHeader("Content-Type", "application/json; charset=utf-8");
        if (!header.equals("")) {
            String[] headerLines = header.split("\\r?\\n");
            for (String headerLine: headerLines) {
                String[] splitedLine = headerLine.split(":", 2);
                if (splitedLine.length == 2) {
                    requestBuilder.setHeader(splitedLine[0].trim(), splitedLine[1].trim());
                }
            }
        }

        requestBuilder.setBody(preparePayload(event, position));
        requestBuilder.execute();
    }

    private byte[] preparePayload(Event event, Position position) {
        Map<String, Object> data = new HashMap<>();
        data.put(KEY_EVENT, event);
        if (position != null) {
            data.put(KEY_POSITION, position);
        }
        if (event.getDeviceId() != 0) {
            Device device = Context.getIdentityManager().getById(event.getDeviceId());
            if (device != null) {
                data.put(KEY_DEVICE, device);
            }
        }
        if (event.getGeofenceId() != 0) {
            Geofence geofence = (Geofence) Context.getGeofenceManager().getById(event.getGeofenceId());
            if (geofence != null) {
                data.put(KEY_GEOFENCE, geofence);
            }
        }
        try {
            return Context.getObjectMapper().writeValueAsString(data).getBytes(StandardCharsets.UTF_8);
        } catch (JsonProcessingException e) {
            Log.warning(e);
            return null;
        }
    }

}
