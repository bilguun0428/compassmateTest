package mn.compassmate.database;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import mn.compassmate.Context;
import mn.compassmate.helper.Log;
import mn.compassmate.model.BaseModel;
import mn.compassmate.model.Permission;
import mn.compassmate.model.User;

public abstract class SimpleObjectManager<T extends BaseModel> extends BaseObjectManager<T>
        implements ManagableObjects {

    private Map<Long, Set<Long>> userItems;

    protected SimpleObjectManager(DataManager dataManager, Class<T> baseClass) {
        super(dataManager, baseClass);
    }

    @Override
    public final Set<Long> getUserItems(long userId) {
        if (!userItems.containsKey(userId)) {
            userItems.put(userId, new HashSet<Long>());
        }
        return userItems.get(userId);
    }

    @Override
    public Set<Long> getManagedItems(long userId) {
        Set<Long> result = new HashSet<>();
        result.addAll(getUserItems(userId));
        for (long managedUserId : Context.getUsersManager().getUserItems(userId)) {
            result.addAll(getUserItems(managedUserId));
        }
        return result;
    }

    public final boolean checkItemPermission(long userId, long itemId) {
        return getUserItems(userId).contains(itemId);
    }

    @Override
    public void refreshItems() {
        super.refreshItems();
        refreshUserItems();
    }

    public final void refreshUserItems() {
        if (getDataManager() != null) {
            try {
                if (userItems != null) {
                    userItems.clear();
                } else {
                    userItems = new ConcurrentHashMap<>();
                }
                for (Permission permission : getDataManager().getPermissions(User.class, getBaseClass())) {
                    getUserItems(permission.getOwnerId()).add(permission.getPropertyId());
                }
            } catch (SQLException | ClassNotFoundException error) {
                Log.warning(error);
            }
        }
    }

    @Override
    public void removeItem(long itemId) throws SQLException {
        super.removeItem(itemId);
        refreshUserItems();
    }

}
