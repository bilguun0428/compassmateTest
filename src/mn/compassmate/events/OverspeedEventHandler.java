package mn.compassmate.events;

import java.util.Collections;
import java.util.Map;

import mn.compassmate.BaseEventHandler;
import mn.compassmate.Context;
import mn.compassmate.model.Device;
import mn.compassmate.model.DeviceState;
import mn.compassmate.model.Event;
import mn.compassmate.model.Position;

public class OverspeedEventHandler extends BaseEventHandler {

    public static final String ATTRIBUTE_SPEED_LIMIT = "speedLimit";

    private boolean notRepeat;
    private long minimalDuration;

    public OverspeedEventHandler(long minimalDuration, boolean notRepeat) {
        this.notRepeat = notRepeat;
        this.minimalDuration = minimalDuration;
    }

    private Map<Event, Position> newEvent(DeviceState deviceState, double speedLimit) {
        Position position = deviceState.getOverspeedPosition();
        Event event = new Event(Event.TYPE_DEVICE_OVERSPEED, position.getDeviceId(), position.getId());
        event.set("speed", deviceState.getOverspeedPosition().getSpeed());
        event.set(ATTRIBUTE_SPEED_LIMIT, speedLimit);
        deviceState.setOverspeedState(notRepeat);
        deviceState.setOverspeedPosition(null);
        return Collections.singletonMap(event, position);
    }

    public Map<Event, Position> updateOverspeedState(DeviceState deviceState, double speedLimit) {
        Map<Event, Position> result = null;
        if (deviceState.getOverspeedState() != null && !deviceState.getOverspeedState()
                && deviceState.getOverspeedPosition() != null && speedLimit != 0) {
            long currentTime = System.currentTimeMillis();
            Position overspeedPosition = deviceState.getOverspeedPosition();
            long overspeedTime = overspeedPosition.getFixTime().getTime();
            if (overspeedTime + minimalDuration <= currentTime) {
                result = newEvent(deviceState, speedLimit);
            }
        }
        return result;
    }

    public Map<Event, Position> updateOverspeedState(DeviceState deviceState, Position position, double speedLimit) {
        Map<Event, Position> result = null;

        Boolean oldOverspeed = deviceState.getOverspeedState();

        long currentTime = position.getFixTime().getTime();
        boolean newOverspeed = position.getSpeed() > speedLimit;
        if (newOverspeed && !oldOverspeed) {
            if (deviceState.getOverspeedPosition() == null) {
                deviceState.setOverspeedPosition(position);
            }
        } else if (oldOverspeed && !newOverspeed) {
            deviceState.setOverspeedState(false);
            deviceState.setOverspeedPosition(null);
        } else {
            deviceState.setOverspeedPosition(null);
        }
        Position overspeedPosition = deviceState.getOverspeedPosition();
        if (overspeedPosition != null) {
            long overspeedTime = overspeedPosition.getFixTime().getTime();
            if (newOverspeed && overspeedTime + minimalDuration <= currentTime) {
                result = newEvent(deviceState, speedLimit);
            }
        }
        return result;
    }

    @Override
    protected Map<Event, Position> analyzePosition(Position position) {

        long deviceId = position.getDeviceId();
        Device device = Context.getIdentityManager().getById(deviceId);
        if (device == null) {
            return null;
        }
        if (!Context.getIdentityManager().isLatestPosition(position) || !position.getValid()) {
            return null;
        }

        double speedLimit = Context.getDeviceManager().lookupAttributeDouble(deviceId, ATTRIBUTE_SPEED_LIMIT, 0, false);
        if (speedLimit == 0) {
            return null;
        }

        Map<Event, Position> result = null;
        DeviceState deviceState = Context.getDeviceManager().getDeviceState(deviceId);

        if (deviceState.getOverspeedState() == null) {
            deviceState.setOverspeedState(position.getSpeed() > speedLimit);
        } else {
            result = updateOverspeedState(deviceState, position, speedLimit);
        }

        Context.getDeviceManager().setDeviceState(deviceId, deviceState);
        return result;
    }

}
