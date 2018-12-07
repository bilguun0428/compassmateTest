/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mn.compassmate.reports.model;

import java.util.Date;

/**
 *
 * @author baasanbatpurevjal
 */
public class GeofenceReport {
    private String deviceName;
    private String groupName;
    private String geofenceName;
    private Date entryDate;
    private Date exitDate;
    private long duration;
    private double spentFuel;
    private double distance;
    private long stopDuration;
    private long motionDuration;

    public GeofenceReport() {
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGeofenceName() {
        return geofenceName;
    }

    public void setGeofenceName(String geofenceName) {
        this.geofenceName = geofenceName;
    }

    public Date getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }

    public Date getExitDate() {
        return exitDate;
    }

    public void setExitDate(Date exitDate) {
        this.exitDate = exitDate;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public double getSpentFuel() {
        return spentFuel;
    }

    public void setSpentFuel(double spentFuel) {
        this.spentFuel = spentFuel;
    }

    public double getDistance() {
       return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public long getStopDuration() {
        return stopDuration;
    }

    public void setStopDuration(long stopDuration) {
        this.stopDuration = stopDuration;
    }

    public long getMotionDuration() {
        return motionDuration;
    }

    public void setMotionDuration(long motionDuration) {
        this.motionDuration = motionDuration;
    }

}
