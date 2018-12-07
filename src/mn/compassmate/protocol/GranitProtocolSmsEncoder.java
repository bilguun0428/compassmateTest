package mn.compassmate.protocol;

import mn.compassmate.StringProtocolEncoder;
import mn.compassmate.helper.Log;
import mn.compassmate.model.Command;

public class GranitProtocolSmsEncoder extends StringProtocolEncoder {

    @Override
    protected String encodeCommand(Command command) {
        switch (command.getType()) {
        case Command.TYPE_REBOOT_DEVICE:
            return "BB+RESET";
        case Command.TYPE_POSITION_PERIODIC:
            return formatCommand(command, "BB+BBMD={%s}", Command.KEY_FREQUENCY);
        default:
            Log.warning(new UnsupportedOperationException(command.getType()));
            return null;
        }
    }

}
