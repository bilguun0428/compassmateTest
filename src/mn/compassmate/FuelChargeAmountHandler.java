/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mn.compassmate;

import mn.compassmate.events.FuelChargeEventHandler;
import mn.compassmate.model.Device;
import mn.compassmate.model.Position;

/**
 *
 * @author baasanbatpurevjal
 */
public class FuelChargeAmountHandler extends BaseDataHandler {

    public FuelChargeAmountHandler() {
    }

    private Position getLastPosition(long deviceId) {
        if (Context.getIdentityManager() != null) {
            return Context.getIdentityManager().getLastPosition(deviceId);
        }
        return null;
    }

    @Override
    protected Position handlePosition(Position position) {
        double currentChargedAmount = 0.0;
        Device device = Context.getIdentityManager().getById(position.getDeviceId());
        if (device != null) {
            double fuelChargeThreshold = Context.getDeviceManager()
                    .lookupAttributeDouble(device.getId(),
                            FuelChargeEventHandler.ATTRIBUTE_FUEL_CHARGE_THRESHOLD, 0, false);
            Position lastPosition = getLastPosition(device.getId());
            if (lastPosition != null && lastPosition.getAttributes().containsKey(Position.KEY_FUEL_CHARGED)) {
                currentChargedAmount = lastPosition.getDouble(Position.KEY_FUEL_CHARGED);
            }
            if (fuelChargeThreshold > 0) {
                if (lastPosition != null) {
                    double currentFuel = 0.0;
                    if (position.getAttributes().containsKey(Position.KEY_FUEL_LEVEL)) {
                        currentFuel = position.getDouble(Position.KEY_FUEL_LEVEL);
                    }
                    double lastFuel = 0.0;
                    if (lastPosition.getAttributes().containsKey(Position.KEY_FUEL_LEVEL)) {
                        lastFuel = lastPosition.getDouble(Position.KEY_FUEL_LEVEL);
                    }
                    double diff = currentFuel - lastFuel;
                    if (diff >= fuelChargeThreshold) {
                        currentChargedAmount = currentChargedAmount + diff;
                    }
                    if (diff < 0.0) {
                        currentChargedAmount = 0.0;
                    }
                }
            }
        }

        position.set(Position.KEY_FUEL_CHARGED, currentChargedAmount);
        return position;
    }

}
