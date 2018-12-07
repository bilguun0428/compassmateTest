package mn.compassmate.api;

import java.sql.SQLException;
import java.util.Set;

import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import mn.compassmate.Context;
import mn.compassmate.database.BaseObjectManager;
import mn.compassmate.database.ExtendedObjectManager;
import mn.compassmate.database.ManagableObjects;
import mn.compassmate.database.SimpleObjectManager;
import mn.compassmate.model.BaseModel;
import mn.compassmate.model.Device;
import mn.compassmate.model.Group;
import mn.compassmate.model.User;

public abstract class BaseObjectResource<T extends BaseModel> extends BaseResource {

    private Class<T> baseClass;

    public BaseObjectResource(Class<T> baseClass) {
        this.baseClass = baseClass;
    }

    protected final Class<T> getBaseClass() {
        return baseClass;
    }

    protected final Set<Long> getSimpleManagerItems(BaseObjectManager<T> manager, boolean all,  long userId) {
        Set<Long> result = null;
        if (all) {
            if (Context.getPermissionsManager().isAdmin(getUserId())) {
                result = manager.getAllItems();
            } else {
                Context.getPermissionsManager().checkManager(getUserId());
                result = ((ManagableObjects) manager).getManagedItems(getUserId());
            }
        } else {
            if (userId == 0) {
                userId = getUserId();
            }
            Context.getPermissionsManager().checkUser(getUserId(), userId);
            result = ((ManagableObjects) manager).getUserItems(userId);
        }
        return result;
    }

    @POST
    public Response add(T entity) throws SQLException {
        Context.getPermissionsManager().checkReadonly(getUserId());
        if (baseClass.equals(Device.class)) {
            Context.getPermissionsManager().checkDeviceReadonly(getUserId());
            Context.getPermissionsManager().checkDeviceLimit(getUserId());
        }

        BaseObjectManager<T> manager = Context.getManager(baseClass);
        manager.addItem(entity);

        Context.getDataManager().linkObject(User.class, getUserId(), baseClass, entity.getId(), true);

        if (manager instanceof SimpleObjectManager) {
            ((SimpleObjectManager<T>) manager).refreshUserItems();
        } else if (baseClass.equals(Group.class) || baseClass.equals(Device.class)) {
            Context.getPermissionsManager().refreshDeviceAndGroupPermissions();
            Context.getPermissionsManager().refreshAllExtendedPermissions();
        }
        return Response.ok(entity).build();
    }

    @Path("{id}")
    @PUT
    public Response update(T entity) throws SQLException {
        Context.getPermissionsManager().checkReadonly(getUserId());
        if (baseClass.equals(Device.class)) {
            Context.getPermissionsManager().checkDeviceReadonly(getUserId());
        } else if (baseClass.equals(User.class)) {
            User before = Context.getPermissionsManager().getUser(entity.getId());
            Context.getPermissionsManager().checkUserUpdate(getUserId(), before, (User) entity);
        }
        Context.getPermissionsManager().checkPermission(baseClass, getUserId(), entity.getId());

        Context.getManager(baseClass).updateItem(entity);

        if (baseClass.equals(Group.class) || baseClass.equals(Device.class)) {
            Context.getPermissionsManager().refreshDeviceAndGroupPermissions();
            Context.getPermissionsManager().refreshAllExtendedPermissions();
        } else if (baseClass.equals(User.class) && Context.getNotificationManager() != null) {
            Context.getNotificationManager().refresh();
        }
        return Response.ok(entity).build();
    }

    @Path("{id}")
    @DELETE
    public Response remove(@PathParam("id") long id) throws SQLException {
        Context.getPermissionsManager().checkReadonly(getUserId());
        if (baseClass.equals(Device.class)) {
            Context.getPermissionsManager().checkDeviceReadonly(getUserId());
        }
        Context.getPermissionsManager().checkPermission(baseClass, getUserId(), id);

        BaseObjectManager<T> manager = Context.getManager(baseClass);
        manager.removeItem(id);

        if (manager instanceof SimpleObjectManager) {
            ((SimpleObjectManager<T>) manager).refreshUserItems();
            if (manager instanceof ExtendedObjectManager) {
                ((ExtendedObjectManager<T>) manager).refreshExtendedPermissions();
            }
        }
        if (baseClass.equals(Group.class) || baseClass.equals(Device.class) || baseClass.equals(User.class)) {
            Context.getPermissionsManager().refreshDeviceAndGroupPermissions();
            if (baseClass.equals(User.class)) {
                Context.getPermissionsManager().refreshAllUsersPermissions();
            } else {
                Context.getPermissionsManager().refreshAllExtendedPermissions();
            }
        }
        // Next should be removed with Attribute Aliases
        if (baseClass.equals(Device.class)) {
            Context.getAliasesManager().removeDevice(id);
        }
        return Response.noContent().build();
    }

}
