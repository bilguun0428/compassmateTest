/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mn.compassmate.reports.model;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author baasanbatpurevjal
 */
public class DailyReport {
	/*
    private Collection<DailyReportDetail> dailyReportDetails;

    public void setDailyReportDetails(ArrayList<DailyReportDetail> dailyReportDetails) {
        this.dailyReportDetails = dailyReportDetails;
    }

    public Collection<DailyReportDetail> getDailyReportDetails() {
        return dailyReportDetails;
    }
	*/
    private String deviceName;

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceName() {
        return deviceName;
    }

    private long deviceId;

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }

    public long getDeviceId() {
        return deviceId;
    }
    private int resCount;
    
    public int getResCount() {
    	return resCount;
    }
    public void setResCount(int resCount) {
    	this.resCount = resCount;
    }
    
    private int startTime;
    
    public int getStartTime() {
    	return startTime;
    }
    public void setStartTime(int entryDate) {
    	this.startTime = entryDate;
    }
    
    private int endTime;
    
    public int getEndTime() {
    	return endTime;
    }
    public void setEndTime(int entryDate) {
    	this.endTime = entryDate;
    }
    private int resDuration;
    
    public int resDuration() {
    	return resDuration;
    }
    public void setResDuration(int resDuration) {
    	this.resDuration = resDuration();
    }
    
}
