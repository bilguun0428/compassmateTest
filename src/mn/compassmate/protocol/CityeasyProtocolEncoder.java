package mn.compassmate.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import mn.compassmate.BaseProtocolEncoder;
import mn.compassmate.helper.Checksum;
import mn.compassmate.helper.Log;
import mn.compassmate.model.Command;

public class CityeasyProtocolEncoder extends BaseProtocolEncoder {

    private ChannelBuffer encodeContent(int type, ChannelBuffer content) {

        ChannelBuffer buf = ChannelBuffers.dynamicBuffer();

        buf.writeByte('S');
        buf.writeByte('S');
        buf.writeShort(2 + 2 + 2 + content.readableBytes() + 4 + 2 + 2);
        buf.writeShort(type);
        buf.writeBytes(content);
        buf.writeInt(0x0B);
        buf.writeShort(Checksum.crc16(Checksum.CRC16_KERMIT, buf.toByteBuffer()));
        buf.writeByte('\r');
        buf.writeByte('\n');

        return buf;
    }

    @Override
    protected Object encodeCommand(Command command) {

        ChannelBuffer content = ChannelBuffers.dynamicBuffer();

        switch (command.getType()) {
            case Command.TYPE_POSITION_SINGLE:
                return encodeContent(CityeasyProtocolDecoder.MSG_LOCATION_REQUEST, content);
            case Command.TYPE_POSITION_PERIODIC:
                content.writeShort(command.getInteger(Command.KEY_FREQUENCY));
                return encodeContent(CityeasyProtocolDecoder.MSG_LOCATION_INTERVAL, content);
            case Command.TYPE_POSITION_STOP:
                content.writeShort(0);
                return encodeContent(CityeasyProtocolDecoder.MSG_LOCATION_INTERVAL, content);
            case Command.TYPE_SET_TIMEZONE:
                int timezone = command.getInteger(Command.KEY_TIMEZONE);
                if (timezone < 0) {
                    content.writeByte(1);
                } else {
                    content.writeByte(0);
                }
                content.writeShort(Math.abs(timezone) / 60);
                return encodeContent(CityeasyProtocolDecoder.MSG_TIMEZONE, content);
            default:
                Log.warning(new UnsupportedOperationException(command.getType()));
                break;
        }

        return null;
    }

}
