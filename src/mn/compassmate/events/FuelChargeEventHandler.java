/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mn.compassmate.events;

import java.util.Collections;
import java.util.Map;
import mn.compassmate.BaseEventHandler;
import mn.compassmate.Context;
import mn.compassmate.model.Device;
import mn.compassmate.model.Event;
import mn.compassmate.model.Position;

/**
 *
 * @author baasanbatpurevjal
 */
public class FuelChargeEventHandler extends BaseEventHandler {

    public static final String ATTRIBUTE_FUEL_CHARGE_THRESHOLD = "fuelChargeThreshold";

	@Override
	protected Map<Event, Position> analyzePosition(Position position) {
		return null;
	
	}
    
    
    
    
    
    
    
/*
    @Override
    protected Map<Event, Position> analyzePosition(Position position) {

        Device device = Context.getIdentityManager().getById(position.getDeviceId());
        if (device == null) {
            return null;
        }
        if (!Context.getIdentityManager().isLatestPosition(position) || !position.getValid()) {
            return null;
        }

        double fuelChargeThreshold = Context.getDeviceManager()
                .lookupAttributeDouble(device.getId(), ATTRIBUTE_FUEL_CHARGE_THRESHOLD, 0, false);

        if (fuelChargeThreshold > 0) {
            Position lastPosition = Context.getIdentityManager().getLastPosition(position.getDeviceId());

            if (position.getAttributes().containsKey(Position.KEY_FUEL_LEVEL)
                && lastPosition != null && lastPosition.getAttributes().containsKey(Position.KEY_FUEL_LEVEL)) {

                double diff = position.getDouble(Position.KEY_FUEL_LEVEL)
                        - lastPosition.getDouble(Position.KEY_FUEL_LEVEL);
                if (diff >= fuelChargeThreshold) {
                    Event event = new Event(Event.TYPE1_DEVICE_FUEL_CHARGE, position.getDeviceId(), position.getId());
                    event.set(ATTRIBUTE_FUEL_CHARGE_THRESHOLD, fuelChargeThreshold);
                    return Collections.singletonMap(event, position);
                }
            }
        }

        return null;
    }
*/
}
