package mn.compassmate.protocol;

import mn.compassmate.protocol.T800xProtocolEncoder;
import org.junit.Test;
import mn.compassmate.ProtocolTest;
import mn.compassmate.model.Command;

public class T800xProtocolEncoderTest extends ProtocolTest {

    @Test
    public void testEncode() throws Exception {

        T800xProtocolEncoder encoder = new T800xProtocolEncoder();
        
        Command command = new Command();
        command.setDeviceId(1);
        command.setType(Command.TYPE_CUSTOM);
        command.set(Command.KEY_DATA, "RELAY,0000,On#");

        verifyCommand(encoder, command, binary("232381001e000101234567890123450152454c41592c303030302c4f6e23"));

    }

}
