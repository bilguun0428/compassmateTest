package mn.compassmate.protocol;

import mn.compassmate.protocol.CellocatorProtocolEncoder;
import org.junit.Ignore;
import org.junit.Test;
import mn.compassmate.ProtocolTest;
import mn.compassmate.model.Command;

public class CellocatorProtocolEncoderTest extends ProtocolTest {

    @Ignore
    @Test
    public void testEncode() throws Exception {

        CellocatorProtocolEncoder encoder = new CellocatorProtocolEncoder();
        
        Command command = new Command();
        command.setDeviceId(1);
        command.setType(Command.TYPE_OUTPUT_CONTROL);
        command.set(Command.KEY_INDEX, 0);
        command.set(Command.KEY_DATA, "1");

        verifyCommand(encoder, command, binary("4D434750000000000000000000000303101000000000000026"));

    }

}
