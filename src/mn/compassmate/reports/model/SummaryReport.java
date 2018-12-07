package mn.compassmate.reports.model;

public class SummaryReport extends BaseReport {
	
	private double overSpeedDistance;
	
	public double getOverSpeedDistance() {
		return overSpeedDistance;
	}
	public void setOverSpeedDistance(double overSpeedDistance) {
		this.overSpeedDistance = overSpeedDistance;
	}
	public void addOverSpeedDistance(double overSpeedDistance) {
		this.overSpeedDistance += overSpeedDistance;
	}
	private double resCount;
	
	public double getresCount() {
		return resCount;
	}
	public void setRescount(double resCount) {
		this.resCount = resCount;
	}
	
	private long activeStopTime;
	
	public void setActiveStopTime(long activeStopTime) {
		this.activeStopTime = activeStopTime;
	}
	
	public long getActiveStopTime() {
		return activeStopTime;
	}
	private long activeMoveTime;
	
	public long getActiveMoveTime() {
		return activeMoveTime;
	}
	
	public void setActiveMoveTime(long activeMoveTime) {
		this.activeMoveTime = activeMoveTime;
	}
	
	public void addActiveMoveTime(long activeMoveTime) {
		this.activeMoveTime += activeMoveTime;
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
