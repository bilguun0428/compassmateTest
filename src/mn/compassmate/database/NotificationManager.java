package mn.compassmate.database;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import mn.compassmate.Context;
import mn.compassmate.helper.Log;
import mn.compassmate.model.Event;
import mn.compassmate.model.Notification;
import mn.compassmate.model.Position;
import mn.compassmate.notification.NotificationMail;
import mn.compassmate.notification.NotificationSms;

public class NotificationManager {

    private final DataManager dataManager;

    private final Map<Long, Set<Notification>> userNotifications = new HashMap<>();

    private final ReadWriteLock notificationsLock = new ReentrantReadWriteLock();

    public NotificationManager(DataManager dataManager) {
        this.dataManager = dataManager;
        refresh();
    }

    public void updateEvent(Event event, Position position) {
        try {
            dataManager.addObject(event);
        } catch (SQLException error) {
            Log.warning(error);
        }

        Set<Long> users = Context.getPermissionsManager().getDeviceUsers(event.getDeviceId());
        for (long userId : users) {
            if (event.getGeofenceId() == 0 || Context.getGeofenceManager() != null
                    && Context.getGeofenceManager().checkItemPermission(userId, event.getGeofenceId())) {
                Notification notification = getUserNotificationByType(userId, event.getType());
                if (notification != null) {
                    if (notification.getWeb()) {
                        Context.getConnectionManager().updateEvent(userId, event);
                    }
                    if (notification.getMail()) {
                        NotificationMail.sendMailAsync(userId, event, position);
                    }
                    if (notification.getSms()) {
                        NotificationSms.sendSmsAsync(userId, event, position);
                    }
                }
            }
        }
        if (Context.getEventForwarder() != null) {
            Context.getEventForwarder().forwardEvent(event, position);
        }
    }

    public void updateEvents(Map<Event, Position> events) {
        for (Entry<Event, Position> event : events.entrySet()) {
            updateEvent(event.getKey(), event.getValue());
        }
    }

    private Set<Notification> getUserNotificationsUnsafe(long userId) {
        if (!userNotifications.containsKey(userId)) {
            userNotifications.put(userId, new HashSet<Notification>());
        }
        return userNotifications.get(userId);
    }

    public Set<Notification> getUserNotifications(long userId) {
        notificationsLock.readLock().lock();
        try {
            return getUserNotificationsUnsafe(userId);
        } finally {
            notificationsLock.readLock().unlock();
        }
    }

    public final void refresh() {
        if (dataManager != null) {
            try {
                notificationsLock.writeLock().lock();
                try {
                    userNotifications.clear();
                    for (Notification notification : dataManager.getObjects(Notification.class)) {
                        getUserNotificationsUnsafe(notification.getUserId()).add(notification);
                    }
                } finally {
                    notificationsLock.writeLock().unlock();
                }
            } catch (SQLException error) {
                Log.warning(error);
            }
        }
    }

    public Notification getUserNotificationByType(long userId, String type) {
        notificationsLock.readLock().lock();
        try {
            for (Notification notification : getUserNotificationsUnsafe(userId)) {
                if (notification.getType().equals(type)) {
                    return notification;
                }
            }
        } finally {
            notificationsLock.readLock().unlock();
        }
        return null;
    }

    public void updateNotification(Notification notification) {
        Notification cachedNotification = getUserNotificationByType(notification.getUserId(), notification.getType());
        if (cachedNotification != null) {
            if (cachedNotification.getWeb() != notification.getWeb()
                    || cachedNotification.getMail() != notification.getMail()
                    || cachedNotification.getSms() != notification.getSms()) {
                if (!notification.getWeb() && !notification.getMail() && !notification.getSms()) {
                    try {
                        dataManager.removeObject(Notification.class, cachedNotification.getId());
                    } catch (SQLException error) {
                        Log.warning(error);
                    }
                    notificationsLock.writeLock().lock();
                    try {
                        getUserNotificationsUnsafe(notification.getUserId()).remove(cachedNotification);
                    } finally {
                        notificationsLock.writeLock().unlock();
                    }
                } else {
                    notificationsLock.writeLock().lock();
                    try {
                        cachedNotification.setWeb(notification.getWeb());
                        cachedNotification.setMail(notification.getMail());
                        cachedNotification.setSms(notification.getSms());
                        cachedNotification.setAttributes(notification.getAttributes());
                    } finally {
                        notificationsLock.writeLock().unlock();
                    }
                    try {
                        dataManager.updateObject(cachedNotification);
                    } catch (SQLException error) {
                        Log.warning(error);
                    }
                }
            } else {
                notification.setId(cachedNotification.getId());
            }
        } else if (notification.getWeb() || notification.getMail() || notification.getSms()) {
            try {
                dataManager.addObject(notification);
            } catch (SQLException error) {
                Log.warning(error);
            }
            notificationsLock.writeLock().lock();
            try {
                getUserNotificationsUnsafe(notification.getUserId()).add(notification);
            } finally {
                notificationsLock.writeLock().unlock();
            }
        }
    }

    public Set<Notification> getAllNotifications() {
        Set<Notification> notifications = new HashSet<>();
        long id = 1;
        Field[] fields = Event.class.getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()) && field.getName().startsWith("TYPE_")) {
                try {
                    Notification notification = new Notification();
                    notification.setType(field.get(null).toString());
                    notification.setId(id++);
                    notifications.add(notification);
                } catch (IllegalArgumentException | IllegalAccessException error) {
                    Log.warning(error);
                }
            }
        }
        return notifications;
    }

    public Collection<Notification> getAllUserNotifications(long userId) {
        Map<String, Notification> notifications = new HashMap<>();
        for (Notification notification : getAllNotifications()) {
            notification.setUserId(userId);
            notifications.put(notification.getType(), notification);
        }
        for (Notification notification : getUserNotifications(userId)) {
            notifications.put(notification.getType(), notification);
        }
        return notifications.values();
    }

}
