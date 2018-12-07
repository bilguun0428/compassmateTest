package mn.compassmate.events;

import java.util.Collections;
import java.util.Map;

import mn.compassmate.BaseEventHandler;
import mn.compassmate.model.Event;
import mn.compassmate.model.Position;

public class AlertEventHandler extends BaseEventHandler {

    @Override
    protected Map<Event, Position> analyzePosition(Position position) {
        Object alarm = position.getAttributes().get(Position.KEY_ALARM);
        if (alarm != null) {
            Event event = new Event(Event.TYPE_ALARM, position.getDeviceId(), position.getId());
            event.set(Position.KEY_ALARM, (String) alarm);
            return Collections.singletonMap(event, position);
        }
        return null;
    }

}
