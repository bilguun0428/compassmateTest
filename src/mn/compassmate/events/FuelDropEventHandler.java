package mn.compassmate.events;

import mn.compassmate.BaseEventHandler;
import mn.compassmate.Context;
import mn.compassmate.model.Device;
import mn.compassmate.model.Event;
import mn.compassmate.model.Position;

import java.util.Collections;
import java.util.Map;

public class FuelDropEventHandler extends BaseEventHandler {

    public static final String ATTRIBUTE_FUEL_DROP_THRESHOLD = "fuelDropThreshold";
    
    

    
    
    @Override
    protected Map<Event, Position> analyzePosition(Position position) {
    	 
        Device device = Context.getIdentityManager().getById(position.getDeviceId());
       
          if (device == null) {
            return null;
        }
        if (!Context.getIdentityManager().isLatestPosition(position) || !position.getValid()) {
            return null;
        }
        
        double fuelDropThreshold = Context.getDeviceManager()
                .lookupAttributeDouble(device.getId(), ATTRIBUTE_FUEL_DROP_THRESHOLD, 0, false);
        
        if (fuelDropThreshold > 0) {
            Position lastPosition = Context.getIdentityManager().getLastPosition(position.getDeviceId());
            if (position.getAttributes().containsKey(Position.KEY_FUEL_LEVEL)
                    && lastPosition != null && lastPosition.getAttributes().containsKey(Position.KEY_FUEL_LEVEL)) {

                double drop = lastPosition.getDouble(Position.KEY_FUEL_LEVEL)
                        - position.getDouble(Position.KEY_FUEL_LEVEL);
                if (drop >= fuelDropThreshold) {
                    Event event = new Event(Event.TYPE1_DEVICE_FUEL_DROP, position.getDeviceId(), position.getId());
                    event.set(ATTRIBUTE_FUEL_DROP_THRESHOLD, fuelDropThreshold);
                    return Collections.singletonMap(event, position);
                }
            }
        }
		
        return null;
    }

}
