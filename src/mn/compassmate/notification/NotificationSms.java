package mn.compassmate.notification;

import mn.compassmate.Context;
import mn.compassmate.model.Event;
import mn.compassmate.model.Position;
import mn.compassmate.model.User;

import com.cloudhopper.smpp.type.RecoverablePduException;
import com.cloudhopper.smpp.type.SmppChannelException;
import com.cloudhopper.smpp.type.SmppTimeoutException;
import com.cloudhopper.smpp.type.UnrecoverablePduException;

public final class NotificationSms {

    private NotificationSms() {
    }

    public static void sendSmsAsync(long userId, Event event, Position position) {
        User user = Context.getPermissionsManager().getUser(userId);
        if (Context.getSmppManager() != null && user.getPhone() != null) {
            Context.getStatisticsManager().registerSms();
            Context.getSmppManager().sendMessageAsync(user.getPhone(),
                    NotificationFormatter.formatSmsMessage(userId, event, position), false);
        }
    }

    public static void sendSmsSync(long userId, Event event, Position position) throws RecoverablePduException,
            UnrecoverablePduException, SmppTimeoutException, SmppChannelException, InterruptedException {
        User user = Context.getPermissionsManager().getUser(userId);
        if (Context.getSmppManager() != null && user.getPhone() != null) {
            Context.getStatisticsManager().registerSms();
            Context.getSmppManager().sendMessageSync(user.getPhone(),
                    NotificationFormatter.formatSmsMessage(userId, event, position), false);
        }
    }
}
