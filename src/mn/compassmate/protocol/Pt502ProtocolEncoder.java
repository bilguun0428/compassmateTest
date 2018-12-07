package mn.compassmate.protocol;

import mn.compassmate.StringProtocolEncoder;
import mn.compassmate.helper.Log;
import mn.compassmate.model.Command;

public class Pt502ProtocolEncoder extends StringProtocolEncoder {

    @Override
    protected Object encodeCommand(Command command) {

        switch (command.getType()) {
            case Command.TYPE_OUTPUT_CONTROL:
                return formatCommand(command, "#OPC{%s},{%s}\r\n", Command.KEY_INDEX, Command.KEY_DATA);
            case Command.TYPE_SET_TIMEZONE:
                return formatCommand(command, "#TMZ{%s}\r\n", Command.KEY_TIMEZONE);
            case Command.TYPE_ALARM_SPEED:
                return formatCommand(command, "#SPD{%s}\r\n", Command.KEY_DATA);
            case Command.TYPE_REQUEST_PHOTO:
                return formatCommand(command, "#PHO\r\n");
            default:
                Log.warning(new UnsupportedOperationException(command.getType()));
                break;
        }

        return null;
    }

}
