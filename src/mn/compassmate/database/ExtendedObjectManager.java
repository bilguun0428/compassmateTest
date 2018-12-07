package mn.compassmate.database;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import mn.compassmate.Context;
import mn.compassmate.helper.Log;
import mn.compassmate.model.Device;
import mn.compassmate.model.Group;
import mn.compassmate.model.Permission;
import mn.compassmate.model.BaseModel;

public abstract class ExtendedObjectManager<T extends BaseModel> extends SimpleObjectManager<T> {

    private final Map<Long, Set<Long>> deviceItems = new ConcurrentHashMap<>();
    private final Map<Long, Set<Long>> deviceItemsWithGroups = new ConcurrentHashMap<>();
    private final Map<Long, Set<Long>> groupItems = new ConcurrentHashMap<>();

    protected ExtendedObjectManager(DataManager dataManager, Class<T> baseClass) {
        super(dataManager, baseClass);
        refreshExtendedPermissions();
    }

    public final Set<Long> getGroupItems(long groupId) {
        if (!groupItems.containsKey(groupId)) {
            groupItems.put(groupId, new HashSet<Long>());
        }
        return groupItems.get(groupId);
    }

    public final Set<Long> getDeviceItems(long deviceId) {
        if (!deviceItems.containsKey(deviceId)) {
            deviceItems.put(deviceId, new HashSet<Long>());
        }
        return deviceItems.get(deviceId);
    }

    public Set<Long> getAllDeviceItems(long deviceId) {
        if (!deviceItemsWithGroups.containsKey(deviceId)) {
            deviceItemsWithGroups.put(deviceId, new HashSet<Long>());
        }
        return deviceItemsWithGroups.get(deviceId);
    }

    @Override
    public void removeItem(long itemId) throws SQLException {
        super.removeItem(itemId);
        refreshExtendedPermissions();
    }

    public void refreshExtendedPermissions() {
        if (getDataManager() != null) {
            try {

                Collection<Permission> databaseGroupPermissions =
                        getDataManager().getPermissions(Group.class, getBaseClass());

                groupItems.clear();
                for (Permission groupPermission : databaseGroupPermissions) {
                    getGroupItems(groupPermission.getOwnerId()).add(groupPermission.getPropertyId());
                }

                Collection<Permission> databaseDevicePermissions =
                        getDataManager().getPermissions(Device.class, getBaseClass());

                deviceItems.clear();
                deviceItemsWithGroups.clear();

                for (Permission devicePermission : databaseDevicePermissions) {
                    getDeviceItems(devicePermission.getOwnerId()).add(devicePermission.getPropertyId());
                    getAllDeviceItems(devicePermission.getOwnerId()).add(devicePermission.getPropertyId());
                }

                for (Device device : Context.getDeviceManager().getAllDevices()) {
                    long groupId = device.getGroupId();
                    while (groupId != 0) {
                        getAllDeviceItems(device.getId()).addAll(getGroupItems(groupId));
                        Group group = (Group) Context.getGroupsManager().getById(groupId);
                        if (group != null) {
                            groupId = group.getGroupId();
                        } else {
                            groupId = 0;
                        }
                    }
                }

            } catch (SQLException | ClassNotFoundException error) {
                Log.warning(error);
            }
        }
    }
}
