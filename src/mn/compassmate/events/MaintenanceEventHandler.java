package mn.compassmate.events;

import java.util.Collections;
import java.util.Map;

import mn.compassmate.BaseEventHandler;
import mn.compassmate.Context;
import mn.compassmate.model.Device;
import mn.compassmate.model.Event;
import mn.compassmate.model.Position;

public class MaintenanceEventHandler extends BaseEventHandler {

    public static final String ATTRIBUTE_MAINTENANCE_START = "maintenance.start";
    public static final String ATTRIBUTE_MAINTENANCE_INTERVAL = "maintenance.interval";

    @Override
    protected Map<Event, Position> analyzePosition(Position position) {
        Device device = Context.getIdentityManager().getById(position.getDeviceId());
        if (device == null || !Context.getIdentityManager().isLatestPosition(position)) {
            return null;
        }

        double maintenanceInterval = Context.getDeviceManager()
                .lookupAttributeDouble(device.getId(), ATTRIBUTE_MAINTENANCE_INTERVAL, 0, false);
        if (maintenanceInterval == 0) {
            return null;
        }
        double maintenanceStart = Context.getDeviceManager()
                .lookupAttributeDouble(device.getId(), ATTRIBUTE_MAINTENANCE_START, 0, false);

        double oldTotalDistance = 0.0;
        double newTotalDistance = 0.0;

        Position lastPosition = Context.getIdentityManager().getLastPosition(position.getDeviceId());
        if (lastPosition != null) {
            oldTotalDistance = lastPosition.getDouble(Position.KEY_TOTAL_DISTANCE);
        }
        newTotalDistance = position.getDouble(Position.KEY_TOTAL_DISTANCE);

        oldTotalDistance -= maintenanceStart;
        newTotalDistance -= maintenanceStart;

        if ((long) (oldTotalDistance / maintenanceInterval) < (long) (newTotalDistance / maintenanceInterval)) {
            Event event = new Event(Event.TYPE1_MAINTENANCE, position.getDeviceId(), position.getId());
            event.set(Position.KEY_TOTAL_DISTANCE, newTotalDistance);
            return Collections.singletonMap(event, position);
        }

        return null;
    }

}
