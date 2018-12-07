package mn.compassmate.protocol;

import mn.compassmate.protocol.HuabaoProtocolEncoder;
import org.junit.Ignore;
import org.junit.Test;
import mn.compassmate.ProtocolTest;
import mn.compassmate.model.Command;

public class HuabaoProtocolEncoderTest extends ProtocolTest {

    @Ignore
    @Test
    public void testEncode() throws Exception {

        HuabaoProtocolEncoder encoder = new HuabaoProtocolEncoder();
        
        Command command = new Command();
        command.setDeviceId(1);
        command.setType(Command.TYPE_ENGINE_STOP);

        verifyCommand(encoder, command, binary("7EA0060007001403305278017701150424154610AD7E"));

    }

}
