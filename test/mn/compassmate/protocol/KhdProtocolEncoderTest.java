package mn.compassmate.protocol;

import mn.compassmate.protocol.KhdProtocolEncoder;
import org.junit.Test;
import mn.compassmate.ProtocolTest;
import mn.compassmate.model.Command;

public class KhdProtocolEncoderTest extends ProtocolTest {

    @Test
    public void testEncode() throws Exception {

        KhdProtocolEncoder encoder = new KhdProtocolEncoder();
        
        Command command = new Command();
        command.setDeviceId(1);
        command.setType(Command.TYPE_ENGINE_STOP);

        verifyCommand(encoder, command, binary("2929390006000000003F0D"));

    }

}
