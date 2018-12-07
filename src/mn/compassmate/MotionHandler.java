package mn.compassmate;

import mn.compassmate.model.Position;

public class MotionHandler extends BaseDataHandler {

    private double speedThreshold;

    public MotionHandler(double speedThreshold) {
        this.speedThreshold = speedThreshold;
    }

    @Override
    protected Position handlePosition(Position position) {
        if (!position.getAttributes().containsKey(Position.KEY_MOTION)) {
            position.set(Position.KEY_MOTION, position.getSpeed() > speedThreshold);
        }
        return position;
    }

}
