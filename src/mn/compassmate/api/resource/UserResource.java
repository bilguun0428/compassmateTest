package mn.compassmate.api.resource;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.security.PermitAll;
import javax.mail.MessagingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import mn.compassmate.Context;
import mn.compassmate.api.BaseObjectResource;
import mn.compassmate.database.UsersManager;
import mn.compassmate.helper.RandomString;
import mn.compassmate.model.ManagedUser;
import mn.compassmate.model.User;
import mn.compassmate.notification.NotificationMail;

@Path("users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource extends BaseObjectResource<User> {

    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
        Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private boolean isValidEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
        return matcher.find();
    }

    public UserResource() {
        super(User.class);
    }

    @GET
    public Collection<User> get(@QueryParam("userId") long userId) {
        UsersManager usersManager = Context.getUsersManager();
        Set<Long> result = null;
        if (Context.getPermissionsManager().isAdmin(getUserId())) {
            if (userId != 0) {
                result = usersManager.getUserItems(userId);
            } else {
                result = usersManager.getAllItems();
            }
        } else if (Context.getPermissionsManager().isManager(getUserId())) {
            result = usersManager.getManagedItems(getUserId());
        } else {
            throw new SecurityException("Admin or manager access required");
        }
        return usersManager.getItems(result);
    }

    @Override
    @PermitAll
    @POST
    public Response add(User entity) throws SQLException {
        if (!Context.getPermissionsManager().isAdmin(getUserId())) {
            Context.getPermissionsManager().checkUserUpdate(getUserId(), new User(), entity);
            if (Context.getPermissionsManager().isManager(getUserId())) {
                Context.getPermissionsManager().checkUserLimit(getUserId());
            } else {
                Context.getPermissionsManager().checkRegistration(getUserId());
                entity.setDeviceLimit(Context.getConfig().getInteger("users.defaultDeviceLimit", -1));
                int expirationDays = Context.getConfig().getInteger("users.defaultExpirationDays");
                if (expirationDays > 0) {
                    entity.setExpirationTime(
                        new Date(System.currentTimeMillis() + (long) expirationDays * 24 * 3600 * 1000));
                }
            }
        }
        Context.getUsersManager().addItem(entity);
        if (Context.getPermissionsManager().isManager(getUserId())) {
            Context.getDataManager().linkObject(User.class, getUserId(), ManagedUser.class, entity.getId(), true);
        }
        Context.getUsersManager().refreshUserItems();
        if (Context.getNotificationManager() != null) {
            Context.getNotificationManager().refresh();
        }
        return Response.ok(entity).build();
    }

    @PermitAll
    @POST
    @Path("/resetPassword")
    public Response resetPassword(User entity) throws SQLException, MessagingException {
        String email = entity.getEmail();
        

        if (isValidEmail(email)) {
            User user = Context.getDataManager().getUserByEmail(email);
            if (user != null) {
                RandomString randomString = new RandomString(8);
                String generatedPassword = randomString.nextString();
                user.setPassword(generatedPassword);
                Context.getUsersManager().updateItem(user);
                Map<String, Object> mapData = new HashMap<>();
                mapData.put("password", generatedPassword);
                NotificationMail.sendMailSync(user.getId(), "resetPassword", mapData);
//                return Response.ok().build();
                return Response.status(Status.OK).build();
            }else {
            
//            	return Response.noContent().build();
            	 return Response.status(Status.NOT_FOUND).build();
//            return Response.status(Response.Status.NOT_FOUND).entity("This email is not found").build();
            }
            
              }

        return Response.notAcceptable(null).entity("Email is not valid").build();
    }

}
