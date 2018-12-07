package mn.compassmate;

import java.util.Map;

import mn.compassmate.model.Event;
import mn.compassmate.model.Position;

public abstract class BaseEventHandler extends BaseDataHandler {

    @Override
    protected Position handlePosition(Position position) {

        Map<Event, Position> events = analyzePosition(position);
        if (events != null && Context.getNotificationManager() != null) {
            Context.getNotificationManager().updateEvents(events);
        }
        return position;
    }

    protected abstract Map<Event, Position> analyzePosition(Position position);

}
