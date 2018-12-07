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
public class DailyReportDetail extends BaseReport {

    private Date startDate;

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    private Date endDate;

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    private String weekDay;

    public String getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(String weekDay) {
        this.weekDay = weekDay;
    }

    private long engineHours;

    public long getEngineHours() {
        return engineHours;
    }

    public void setEngineHours(long engineHours) {
        this.engineHours = engineHours;
    }

    public void addEngineHours(long engineHours) {
        this.engineHours += engineHours;
    }

    private long stoppedDuration;

    public long getStoppedDuration() {
        return stoppedDuration;
    }

    public void setStoppedDuration(long stoppedDuration) {
        this.stoppedDuration = stoppedDuration;
    }
}
