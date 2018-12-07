package mn.compassmate.protocol;

import mn.compassmate.StringProtocolEncoder;
import mn.compassmate.helper.Log;
import mn.compassmate.model.Command;

public class Tk103ProtocolEncoder extends StringProtocolEncoder {

    @Override
    protected Object encodeCommand(Command command) {

        switch (command.getType()) {
            case Command.TYPE_GET_VERSION:
                return formatCommand(command, "({%s}AP07)", Command.KEY_UNIQUE_ID);
            case Command.TYPE_REBOOT_DEVICE:
                return formatCommand(command, "({%s}AT00)", Command.KEY_UNIQUE_ID);
            case Command.TYPE_SET_ODOMETER:
                return formatCommand(command, "({%s}AX01)", Command.KEY_UNIQUE_ID);
            case Command.TYPE_POSITION_SINGLE:
                return formatCommand(command, "({%s}AP00)", Command.KEY_UNIQUE_ID);
            case Command.TYPE_POSITION_PERIODIC:
                return formatCommand(command, "({%s}AR00%s0000)", Command.KEY_UNIQUE_ID,
                        String.format("%04X", command.getInteger(Command.KEY_FREQUENCY)));
            case Command.TYPE_POSITION_STOP:
                return formatCommand(command, "({%s}AR0000000000)", Command.KEY_UNIQUE_ID);
            case Command.TYPE_ENGINE_STOP:
                return formatCommand(command, "({%s}AV011)", Command.KEY_UNIQUE_ID);
            case Command.TYPE_ENGINE_RESUME:
                return formatCommand(command, "({%s}AV010)", Command.KEY_UNIQUE_ID);
            default:
                Log.warning(new UnsupportedOperationException(command.getType()));
                break;
        }

        return null;
    }

}
