package mn.compassmate.protocol;

import mn.compassmate.protocol.RuptelaProtocolEncoder;
import org.junit.Test;
import mn.compassmate.ProtocolTest;
import mn.compassmate.model.Command;

public class RuptelaProtocolEncoderTest extends ProtocolTest {

    @Test
    public void testEncode() throws Exception {

        RuptelaProtocolEncoder encoder = new RuptelaProtocolEncoder();
        
        Command command = new Command();
        command.setDeviceId(1);
        command.setType(Command.TYPE_CUSTOM);
        command.set(Command.KEY_DATA, " Setio 2,1");

        verifyCommand(encoder, command, binary("000b6c20536574696F20322C31eb3e"));

    }

}
