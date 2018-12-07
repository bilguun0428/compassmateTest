package mn.compassmate.events;

import java.util.Collections;
import java.util.Map;

import mn.compassmate.BaseEventHandler;
import mn.compassmate.model.Event;
import mn.compassmate.model.Position;

public class CommandResultEventHandler extends BaseEventHandler {

    @Override
    protected Map<Event, Position> analyzePosition(Position position) {
        Object commandResult = position.getAttributes().get(Position.KEY_RESULT);
        if (commandResult != null) {
            Event event = new Event(Event.TYPE_COMMAND_RESULT, position.getDeviceId(), position.getId());
            event.set(Position.KEY_RESULT, (String) commandResult);
            return Collections.singletonMap(event, position);
        }
        return null;
    }

}
