package mn.compassmate.events;

import mn.compassmate.Context;
import mn.compassmate.model.Device;
import mn.compassmate.model.Event;

public final class TextMessageEventHandler {

    private TextMessageEventHandler() {
    }

    public static void handleTextMessage(String phone, String message) {
        Device device = Context.getDeviceManager().getDeviceByPhone(phone);
        if (device != null && Context.getNotificationManager() != null) {
            Event event = new Event(Event.TYPE1_TEXT_MESSAGE, device.getId());
            event.set("message", message);
            Context.getNotificationManager().updateEvent(event, null);
        }
    }

}
