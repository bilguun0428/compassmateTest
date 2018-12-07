package mn.compassmate.protocol;

import mn.compassmate.StringProtocolEncoder;
import mn.compassmate.helper.Log;
import mn.compassmate.model.Command;

public class Jt600ProtocolEncoder extends StringProtocolEncoder {
    @Override
    protected Object encodeCommand(Command command) {

        switch (command.getType()) {
            case Command.TYPE_ENGINE_STOP:
                return "(S07,0)";
            case Command.TYPE_ENGINE_RESUME:
                return "(S07,1)";
            case Command.TYPE_SET_TIMEZONE:
                int offset = command.getInteger(Command.KEY_TIMEZONE) / 60;
                return "(S09,1," + offset + ")";
            case Command.TYPE_REBOOT_DEVICE:
                return "(S17)";
            default:
                Log.warning(new UnsupportedOperationException(command.getType()));
                break;
        }

        return null;
    }
}
