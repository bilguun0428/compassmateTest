package mn.compassmate.protocol;

import mn.compassmate.StringProtocolEncoder;
import mn.compassmate.helper.Checksum;
import mn.compassmate.helper.Log;
import mn.compassmate.model.Command;

import java.util.Map;

public class MeitrackProtocolEncoder extends StringProtocolEncoder {

    private Object formatCommand(Command command, char dataId, String content) {
        String uniqueId = getUniqueId(command.getDeviceId());
        int length = 1 + uniqueId.length() + 1 + content.length() + 5;
        String result = String.format("@@%c%02d,%s,%s*", dataId, length, uniqueId, content);
        result += Checksum.sum(result) + "\r\n";
        return result;
    }

    @Override
    protected Object encodeCommand(Command command) {

        Map<String, Object> attributes = command.getAttributes();

        switch (command.getType()) {
            case Command.TYPE_POSITION_SINGLE:
                return formatCommand(command, 'Q', "A10");
            case Command.TYPE_ENGINE_STOP:
                return formatCommand(command, 'M', "C01,0,12222");
            case Command.TYPE_ENGINE_RESUME:
                return formatCommand(command, 'M', "C01,0,02222");
            case Command.TYPE_ALARM_ARM:
                return formatCommand(command, 'M', "C01,0,22122");
            case Command.TYPE_ALARM_DISARM:
                return formatCommand(command, 'M', "C01,0,22022");
            case Command.TYPE_REQUEST_PHOTO:
                return formatCommand(command, 'D', "D03,1,camera_picture.jpg");
            case Command.TYPE_SEND_SMS:
                return formatCommand(command, 'f', "C02,0,"
                        + attributes.get(Command.KEY_PHONE) + "," + attributes.get(Command.KEY_MESSAGE));
            default:
                Log.warning(new UnsupportedOperationException(command.getType()));
                break;
        }

        return null;
    }

}
