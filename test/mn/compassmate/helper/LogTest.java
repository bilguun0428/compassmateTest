package mn.compassmate.helper;

import mn.compassmate.helper.Log;
import org.junit.Assert;
import org.junit.Test;

public class LogTest {
    
    @Test
    public void testLog() {
        Assert.assertEquals("test - Exception (LogTest:11 < ...)", Log.exceptionStack(new Exception("test")));
    }

}
