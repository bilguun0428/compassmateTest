package mn.compassmate.processing;

import mn.compassmate.BaseDataHandler;
import mn.compassmate.Context;
import mn.compassmate.model.Position;

public class CopyAttributesHandler extends BaseDataHandler {

    private Position getLastPosition(long deviceId) {
        if (Context.getIdentityManager() != null) {
            return Context.getIdentityManager().getLastPosition(deviceId);
        }
        return null;
    }

    @Override
    protected Position handlePosition(Position position) {
        String attributesString = Context.getDeviceManager().lookupAttributeString(
                position.getDeviceId(), "processing.copyAttributes", "", true);
        Position last = getLastPosition(position.getDeviceId());
        if (attributesString.isEmpty()) {
            attributesString = Position.KEY_DRIVER_UNIQUE_ID;
        } else {
            attributesString += "," + Position.KEY_DRIVER_UNIQUE_ID;
        }
        if (last != null) {
            for (String attribute : attributesString.split("[ ,]")) {
                if (last.getAttributes().containsKey(attribute) && !position.getAttributes().containsKey(attribute)) {
                    position.getAttributes().put(attribute, last.getAttributes().get(attribute));
                }
            }
        }
        return position;
    }

}
