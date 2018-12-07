package mn.compassmate.api;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;

import mn.compassmate.Context;
import mn.compassmate.database.ExtendedObjectManager;
import mn.compassmate.model.BaseModel;

public class ExtendedObjectResource<T extends BaseModel> extends BaseObjectResource<T> {

    public ExtendedObjectResource(Class<T> baseClass) {
        super(baseClass);
    }

    @GET
    public Collection<T> get(
            @QueryParam("all") boolean all, @QueryParam("userId") long userId, @QueryParam("groupId") long groupId,
            @QueryParam("deviceId") long deviceId, @QueryParam("refresh") boolean refresh) throws SQLException {

        ExtendedObjectManager<T> manager = (ExtendedObjectManager<T>) Context.getManager(getBaseClass());
        if (refresh) {
            manager.refreshItems();
        }

        Set<Long> result = new HashSet<>(getSimpleManagerItems(manager, all, userId));

        if (groupId != 0) {
            Context.getPermissionsManager().checkGroup(getUserId(), groupId);
            result.retainAll(manager.getGroupItems(groupId));
        }

        if (deviceId != 0) {
            Context.getPermissionsManager().checkDevice(getUserId(), deviceId);
            result.retainAll(manager.getDeviceItems(deviceId));
        }
        return manager.getItems(result);

    }

}
