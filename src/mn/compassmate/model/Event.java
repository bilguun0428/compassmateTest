package mn.compassmate.model;

import java.util.Date;

public class Event extends Message {

    public Event(String type, long deviceId, long positionId) {
        this(type, deviceId);
        setPositionId(positionId);
    }

    public Event(String type, long deviceId) {
        setType(type);
        setDeviceId(deviceId);
        this.serverTime = new Date();
    }

    public Event() {
    }

    public static final String ALL_EVENTS = "allEvents";

    public static final String TYPE_COMMAND_RESULT = "commandResult";

    public static final String TYPE_DEVICE_ONLINE = "deviceOnline";
    public static final String TYPE_DEVICE_STANDBY = "deviceStandBy";

    public static final String TYPE_DEVICE_UNKNOWN = "deviceUnknown";
    public static final String TYPE_DEVICE_OFFLINE = "deviceOffline";

    public static final String TYPE1_DEVICE_MOVING = "deviceMoving";
    public static final String TYPE1_DEVICE_STOPPED = "deviceStopped";

    public static final String TYPE_DEVICE_OVERSPEED = "deviceOverspeed";
    public static final String TYPE1_DEVICE_FUEL_DROP = "deviceFuelDrop";
    public static final String TYPE1_DEVICE_FUEL_CHARGE = "deviceFuelCharge";

    public static final String TYPE_GEOFENCE_ENTER = "geofenceEnter";
    public static final String TYPE_GEOFENCE_EXIT = "geofenceExit";

    public static final String TYPE_ALARM = "alarm";
    
    //fuel added event 
    public static final String TYPE_FUEL_ADD = "fueladd";

    public static final String TYPE1_IGNITION_ON = "ignitionOn";
    public static final String TYPE1_IGNITION_OFF = "ignitionOff";

    public static final String TYPE1_MAINTENANCE = "maintenance";

    public static final String TYPE1_TEXT_MESSAGE = "textMessage";

    public static final String TYPE1_DRIVER_CHANGED = "driverChanged";

    private Date serverTime;

    public Date getServerTime() {
        if (serverTime != null) {
            return new Date(serverTime.getTime());
        } else {
            return null;
        }
    }

    public void setServerTime(Date serverTime) {
        if (serverTime != null) {
            this.serverTime = new Date(serverTime.getTime());
        } else {
            this.serverTime = null;
        }
    }

    private long positionId;

    public long getPositionId() {
        return positionId;
    }

    public void setPositionId(long positionId) {
        this.positionId = positionId;
    }

    private long geofenceId = 0;

    public long getGeofenceId() {
        return geofenceId;
    }

    public void setGeofenceId(long geofenceId) {
        this.geofenceId = geofenceId;
    }

}
