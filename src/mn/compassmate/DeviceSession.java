package mn.compassmate;

public class DeviceSession {

    private final long deviceId;

    public DeviceSession(long deviceId) {
        this.deviceId = deviceId;
    }

    public long getDeviceId() {
        return deviceId;
    }

}
