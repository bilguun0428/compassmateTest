package mn.compassmate.protocol;

import mn.compassmate.protocol.TeltonikaProtocolEncoder;
import org.junit.Test;
import mn.compassmate.ProtocolTest;
import mn.compassmate.model.Command;

public class TeltonikaProtocolEncoderTest extends ProtocolTest {

    @Test
    public void testEncode() throws Exception {

        TeltonikaProtocolEncoder encoder = new TeltonikaProtocolEncoder();
        
        Command command = new Command();
        command.setDeviceId(1);
        command.setType(Command.TYPE_CUSTOM);
        command.set(Command.KEY_DATA, "setdigout 11");

        verifyCommand(encoder, command, binary("00000000000000160C01050000000E7365746469676F75742031310D0A010000E258"));

    }

}
