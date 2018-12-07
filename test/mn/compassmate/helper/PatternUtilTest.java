package mn.compassmate.helper;

import mn.compassmate.helper.PatternUtil;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PatternUtilTest {

    @Ignore
    @Test
    public void testCheckPattern() {

        assertEquals("ab", PatternUtil.checkPattern("abc", "abd").getPatternMatch());

    }

}
