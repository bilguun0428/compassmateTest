package mn.compassmate.model;

import mn.compassmate.model.Position;
import mn.compassmate.model.MiscFormatter;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class MiscFormatterTest {

    @Test
    public void testToString() throws Exception {

        Position position = new Position();
        position.set("a", "1");
        position.set("b", "2");
        position.set("a", "3");

        assertEquals("<info><a>3</a><b>2</b></info>", MiscFormatter.toXmlString(position.getAttributes()));
        
    }

}
