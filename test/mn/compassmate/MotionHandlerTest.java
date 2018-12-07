package mn.compassmate;

import mn.compassmate.MotionHandler;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import mn.compassmate.model.Position;

public class MotionHandlerTest {

    @Test
    public void testCalculateMotion() throws Exception {

        MotionHandler motionHandler = new MotionHandler(0.01);

        Position position = motionHandler.handlePosition(new Position());

        assertEquals(false, position.getAttributes().get(Position.KEY_MOTION));

    }

}
