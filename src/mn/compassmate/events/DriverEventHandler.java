package mn.compassmate.events;

import java.util.Collections;
import java.util.Map;

import mn.compassmate.BaseEventHandler;
import mn.compassmate.Context;
import mn.compassmate.model.Event;
import mn.compassmate.model.Position;

public class DriverEventHandler extends BaseEventHandler {

    @Override
    protected Map<Event, Position> analyzePosition(Position position) {
        if (!Context.getIdentityManager().isLatestPosition(position)) {
            return null;
        }
        String driverUniqueId = position.getString(Position.KEY_DRIVER_UNIQUE_ID);
        if (driverUniqueId != null) {
            String oldDriverUniqueId = null;
            Position lastPosition = Context.getIdentityManager().getLastPosition(position.getDeviceId());
            if (lastPosition != null) {
                oldDriverUniqueId = lastPosition.getString(Position.KEY_DRIVER_UNIQUE_ID);
            }
            if (!driverUniqueId.equals(oldDriverUniqueId)) {
                Event event = new Event(Event.TYPE1_DRIVER_CHANGED, position.getDeviceId(), position.getId());
                event.set(Position.KEY_DRIVER_UNIQUE_ID, driverUniqueId);
                return Collections.singletonMap(event, position);
            }
        }
        return null;
    }

}
