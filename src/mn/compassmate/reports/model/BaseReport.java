package mn.compassmate.reports.model;

public class BaseReport {

    private long deviceId;

    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }

    private String deviceName;

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
    
    // added 
    private double overspeedDistance;
    
    public double GetOverspeedDistance() {
    	return overspeedDistance;
    }
    public void setOverSpeedDistance(double overspeedDistance) {
    	this.overspeedDistance = overspeedDistance;
    }
    // end 
    
    private double distance;

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void addDistance(double distance) {
        this.distance += distance;
    }

    private double averageSpeed;

    public double getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(Double averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    private double maxSpeed;

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(double maxSpeed) {
        if (maxSpeed > this.maxSpeed) {
            this.maxSpeed = maxSpeed;
        }
    }

    private double spentFuel;

    public double getSpentFuel() {
        return spentFuel;
    }

    public void setSpentFuel(double spentFuel) {
        this.spentFuel = spentFuel;
    }

    private double chargedFuel;

    public double getChargedFuel() {
        return chargedFuel;
    }

    public void setChargedFuel(double chargedFuel) {
        this.chargedFuel = chargedFuel;
    }

    private long chargedCount;

    public long getChargedCount() {
        return chargedCount;
    }

    public void setChargedCount(long chargedCount) {
        this.chargedCount = chargedCount;
    }
}
