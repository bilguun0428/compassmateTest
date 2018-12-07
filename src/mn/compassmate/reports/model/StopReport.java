package mn.compassmate.reports.model;

import java.util.Date;

public class StopReport extends BaseReport  {

    private long positionId;

    public long getPositionId() {
        return positionId;
    }

    public void setPositionId(long positionId) {
        this.positionId = positionId;
    }

    private double latitude;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    private double longitude;

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
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

    private String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    private long duration;

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    private long engineHours; // milliseconds

    public long getEngineHours() {
        return engineHours;
    }

    public void setEngineHours(long engineHours) {
        this.engineHours = engineHours;
    }

    public void addEngineHours(long engineHours) {
        this.engineHours += engineHours;
    }
}
