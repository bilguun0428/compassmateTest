package mn.compassmate.api.resource;

import java.sql.SQLException;
import java.util.Collection;

import javax.mail.MessagingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import mn.compassmate.Context;
import mn.compassmate.api.BaseResource;
import mn.compassmate.model.Event;
import mn.compassmate.model.Notification;
import mn.compassmate.notification.NotificationMail;
import mn.compassmate.notification.NotificationSms;

import com.cloudhopper.smpp.type.RecoverablePduException;
import com.cloudhopper.smpp.type.SmppChannelException;
import com.cloudhopper.smpp.type.SmppTimeoutException;
import com.cloudhopper.smpp.type.UnrecoverablePduException;

@Path("users/notifications")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NotificationResource extends BaseResource {

    @GET
    public Collection<Notification> get(@QueryParam("all") boolean all,
            @QueryParam("userId") long userId) throws SQLException {
        if (all) {
            return Context.getNotificationManager().getAllNotifications();
        }
        if (userId == 0) {
            userId = getUserId();
        }
        Context.getPermissionsManager().checkUser(getUserId(), userId);
        return Context.getNotificationManager().getAllUserNotifications(userId);
    }

    @POST
    public Response update(Notification entity) throws SQLException {
        Context.getPermissionsManager().checkReadonly(getUserId());
        Context.getPermissionsManager().checkUser(getUserId(), entity.getUserId());
        Context.getNotificationManager().updateNotification(entity);
        return Response.ok(entity).build();
    }

    @Path("test")
    @POST
    public Response testMessage() throws MessagingException, RecoverablePduException,
            UnrecoverablePduException, SmppTimeoutException, SmppChannelException, InterruptedException {
        NotificationMail.sendMailSync(getUserId(), new Event("test", 0), null);
        NotificationSms.sendSmsSync(getUserId(), new Event("test", 0), null);
        return Response.noContent().build();
    }

}
