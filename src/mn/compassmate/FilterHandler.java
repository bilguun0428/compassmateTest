package mn.compassmate;

import mn.compassmate.helper.Log;
import mn.compassmate.helper.UnitsConverter;
import mn.compassmate.model.Position;

public class FilterHandler extends BaseDataHandler {

    private boolean filterInvalid;
    private boolean filterZero;
    private boolean filterDuplicate;
    private long filterFuture;
    private boolean filterApproximate;
    private boolean filterStatic;
    private int filterDistance;
    private int filterMaxSpeed;
    private long skipLimit;
    private boolean skipAlarms;

    public void setFilterInvalid(boolean filterInvalid) {
        this.filterInvalid = filterInvalid;
    }

    public void setFilterZero(boolean filterZero) {
        this.filterZero = filterZero;
    }

    public void setFilterDuplicate(boolean filterDuplicate) {
        this.filterDuplicate = filterDuplicate;
    }

    public void setFilterFuture(long filterFuture) {
        this.filterFuture = filterFuture;
    }

    public void setFilterApproximate(boolean filterApproximate) {
        this.filterApproximate = filterApproximate;
    }

    public void setFilterStatic(boolean filterStatic) {
        this.filterStatic = filterStatic;
    }

    public void setFilterDistance(int filterDistance) {
        this.filterDistance = filterDistance;
    }

    public void setFilterMaxSpeed(int filterMaxSpeed) {
        this.filterMaxSpeed = filterMaxSpeed;
    }

    public void setSkipLimit(long skipLimit) {
        this.skipLimit = skipLimit;
    }

    public void setSkipAlarms(boolean skipAlarms) {
        this.skipAlarms = skipAlarms;
    }

    public FilterHandler() {
        Config config = Context.getConfig();
        if (config != null) {
            filterInvalid = config.getBoolean("filter.invalid");
            filterZero = config.getBoolean("filter.zero");
            filterDuplicate = config.getBoolean("filter.duplicate");
            filterFuture = config.getLong("filter.future") * 1000;
            filterApproximate = config.getBoolean("filter.approximate");
            filterStatic = config.getBoolean("filter.static");
            filterDistance = config.getInteger("filter.distance");
            filterMaxSpeed = config.getInteger("filter.maxSpeed");
            skipLimit = config.getLong("filter.skipLimit") * 1000;
            skipAlarms = config.getBoolean("filter.skipAlarms");
        }
    }

    private boolean filterInvalid(Position position) {
        return filterInvalid && (!position.getValid()
           || position.getLatitude() > 90 || position.getLongitude() > 180
           || position.getLatitude() < -90 || position.getLongitude() < -180);
    }

    private boolean filterZero(Position position) {
        return filterZero && position.getLatitude() == 0.0 && position.getLongitude() == 0.0;
    }

    private boolean filterDuplicate(Position position, Position last) {
        if (filterDuplicate && last != null && position.getFixTime().equals(last.getFixTime())) {
            for (String key : position.getAttributes().keySet()) {
                if (!last.getAttributes().containsKey(key)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private boolean filterFuture(Position position) {
        return filterFuture != 0 && position.getFixTime().getTime() > System.currentTimeMillis() + filterFuture;
    }

    private boolean filterApproximate(Position position) {
        return filterApproximate && position.getBoolean(Position.KEY_APPROXIMATE);
    }

    private boolean filterStatic(Position position) {
        return filterStatic && position.getSpeed() == 0.0;
    }

    private boolean filterDistance(Position position, Position last) {
        if (filterDistance != 0 && last != null) {
            return position.getDouble(Position.KEY_DISTANCE) < filterDistance;
        }
        return false;
    }

    private boolean filterMaxSpeed(Position position, Position last) {
        if (filterMaxSpeed != 0 && last != null) {
            double distance = position.getDouble(Position.KEY_DISTANCE);
            double time = position.getFixTime().getTime() - last.getFixTime().getTime();
            return UnitsConverter.knotsFromMps(distance / (time / 1000)) > filterMaxSpeed;
        }
        return false;
    }

    private boolean skipLimit(Position position, Position last) {
        if (skipLimit != 0 && last != null) {
            return (position.getFixTime().getTime() - last.getFixTime().getTime()) > skipLimit;
        }
        return false;
    }

    private boolean skipAlarms(Position position) {
        return skipAlarms && position.getAttributes().containsKey(Position.KEY_ALARM);
    }

    private boolean filter(Position position) {

        StringBuilder filterType = new StringBuilder();

        Position last = null;
        if (Context.getIdentityManager() != null) {
            last = Context.getIdentityManager().getLastPosition(position.getDeviceId());
        }

        if (skipLimit(position, last) || skipAlarms(position)) {
            return false;
        }

        if (filterInvalid(position)) {
            filterType.append("Invalid ");
        }
        if (filterZero(position)) {
            filterType.append("Zero ");
        }
        if (filterDuplicate(position, last)) {
            filterType.append("Duplicate ");
        }
        if (filterFuture(position)) {
            filterType.append("Future ");
        }
        if (filterApproximate(position)) {
            filterType.append("Approximate ");
        }
        if (filterStatic(position)) {
            filterType.append("Static ");
        }
        if (filterDistance(position, last)) {
            filterType.append("Distance ");
        }
        if (filterMaxSpeed(position, last)) {
            filterType.append("MaxSpeed ");
        }

        if (filterType.length() > 0) {

            StringBuilder message = new StringBuilder();
            message.append("Position filtered by ");
            message.append(filterType.toString());
            message.append("filters from device: ");
            message.append(Context.getIdentityManager().getById(position.getDeviceId()).getUniqueId());
            message.append(" with id: ");
            message.append(position.getDeviceId());

            Log.info(message.toString());
            return true;
        }

        return false;
    }

    @Override
    protected Position handlePosition(Position position) {
        if (filter(position)) {
            return null;
        }
        return position;
    }

}
