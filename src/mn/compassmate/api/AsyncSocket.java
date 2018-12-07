package mn.compassmate.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import mn.compassmate.Context;
import mn.compassmate.database.ConnectionManager;
import mn.compassmate.helper.Log;
import mn.compassmate.model.Device;
import mn.compassmate.model.Event;
import mn.compassmate.model.Position;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AsyncSocket extends WebSocketAdapter implements ConnectionManager.UpdateListener {

    private static final String KEY_DEVICES = "devices";
    private static final String KEY_POSITIONS = "positions";
    private static final String KEY_EVENTS = "events";

    private long userId;

    public AsyncSocket(long userId) {
        this.userId = userId;
    }

    @Override
    public void onWebSocketConnect(Session session) {
        super.onWebSocketConnect(session);

        Map<String, Collection<?>> data = new HashMap<>();
        data.put(KEY_POSITIONS, Context.getDeviceManager().getInitialState(userId));
        sendData(data);

        Context.getConnectionManager().addListener(userId, this);
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        super.onWebSocketClose(statusCode, reason);

        Context.getConnectionManager().removeListener(userId, this);
    }

    @Override
    public void onUpdateDevice(Device device) {
        Map<String, Collection<?>> data = new HashMap<>();
        data.put(KEY_DEVICES, Collections.singletonList(device));
        sendData(data);
    }

    @Override
    public void onUpdatePosition(Position position) {
        Map<String, Collection<?>> data = new HashMap<>();
        data.put(KEY_POSITIONS, Collections.singletonList(position));
        sendData(data);
    }

    @Override
    public void onUpdateEvent(Event event) {
        Map<String, Collection<?>> data = new HashMap<>();
        data.put(KEY_EVENTS, Collections.singletonList(event));
        sendData(data);
    }

    private void sendData(Map<String, Collection<?>> data) {
        if (!data.isEmpty() && isConnected()) {
            try {
                getRemote().sendString(Context.getObjectMapper().writeValueAsString(data), null);
            } catch (JsonProcessingException e) {
                Log.warning(e);
            }
        }
    }
}
