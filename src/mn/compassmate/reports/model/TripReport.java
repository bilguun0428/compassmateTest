package mn.compassmate.reports.model;

import java.util.Date;

public class TripReport extends BaseReport {

    private long startPositionId;

    public long getStartPositionId() {
        return startPositionId;
    }

    public void setStartPositionId(long startPositionId) {
        this.startPositionId = startPositionId;
    }

    private long endPositionId;

    public long getEndPositionId() {
        return endPositionId;
    }

    public void setEndPositionId(long endPositionId) {
        this.endPositionId = endPositionId;
    }

    private double startLat;

    public double getStartLat() {
        return startLat;
    }

    public void setStartLat(double startLat) {
        this.startLat = startLat;
    }

    private double startLon;

    public double getStartLon() {
        return startLon;
    }

    public void setStartLon(double startLon) {
        this.startLon = startLon;
    }

    private double endLat;

    public double getEndLat() {
        return endLat;
    }

    public void setEndLat(double endLat) {
        this.endLat = endLat;
    }

    private double endLon;

    public double getEndLon() {
        return endLon;
    }

    public void setEndLon(double endLon) {
        this.endLon = endLon;
    }

    private Date startTime;

    public Date getStartTime() {
        if (startTime != null) {
            return new Date(startTime.getTime());
        } else {
            return null;
        }
    }

    public void setStartTime(Date startTime) {
        if (startTime != null) {
            this.startTime = new Date(startTime.getTime());
        } else {
            this.startTime = null;
        }
    }

    private String startAddress;

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String address) {
        this.startAddress = address;
    }

    private Date endTime;

    public Date getEndTime() {
        if (endTime != null) {
            return new Date(endTime.getTime());
        } else {
            return null;
        }
    }

    public void setEndTime(Date endTime) {
        if (endTime != null) {
            this.endTime = new Date(endTime.getTime());
        } else {
            this.endTime = null;
        }
    }

    private String endAddress;

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String address) {
        this.endAddress = address;
    }

    private long duration;

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    private String driverUniqueId;

    public String getDriverUniqueId() {
        return driverUniqueId;
    }

    public void setDriverUniqueId(String driverUniqueId) {
        this.driverUniqueId = driverUniqueId;
    }

    private String driverName;

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }
}
