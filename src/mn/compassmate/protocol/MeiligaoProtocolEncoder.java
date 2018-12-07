package mn.compassmate.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import mn.compassmate.BaseProtocolEncoder;
import mn.compassmate.helper.Checksum;
import mn.compassmate.helper.Log;
import mn.compassmate.model.Command;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;

public class MeiligaoProtocolEncoder extends BaseProtocolEncoder {

    public static final int MSG_TRACK_ON_DEMAND = 0x4101;
    public static final int MSG_TRACK_BY_INTERVAL = 0x4102;
    public static final int MSG_MOVEMENT_ALARM = 0x4106;
    public static final int MSG_OUTPUT_CONTROL = 0x4115;
    public static final int MSG_TIME_ZONE = 0x4132;
    public static final int MSG_REBOOT_GPS = 0x4902;

    private ChannelBuffer encodeContent(long deviceId, int type, ChannelBuffer content) {

        ChannelBuffer buf = ChannelBuffers.dynamicBuffer();

        buf.writeByte('@');
        buf.writeByte('@');

        buf.writeShort(2 + 2 + 7 + 2 + content.readableBytes() + 2 + 2); // message length

        buf.writeBytes(DatatypeConverter.parseHexBinary((getUniqueId(deviceId) + "FFFFFFFFFFFFFF").substring(0, 14)));

        buf.writeShort(type);

        buf.writeBytes(content);

        buf.writeShort(Checksum.crc16(Checksum.CRC16_CCITT_FALSE, buf.toByteBuffer()));

        buf.writeByte('\r');
        buf.writeByte('\n');

        return buf;
    }

    @Override
    protected Object encodeCommand(Command command) {

        ChannelBuffer content = ChannelBuffers.dynamicBuffer();

        switch (command.getType()) {
            case Command.TYPE_POSITION_SINGLE:
                return encodeContent(command.getDeviceId(), MSG_TRACK_ON_DEMAND, content);
            case Command.TYPE_POSITION_PERIODIC:
                content.writeShort(command.getInteger(Command.KEY_FREQUENCY) / 10);
                return encodeContent(command.getDeviceId(), MSG_TRACK_BY_INTERVAL, content);
            case Command.TYPE_ENGINE_STOP:
                content.writeByte(0x01);
                return encodeContent(command.getDeviceId(), MSG_OUTPUT_CONTROL, content);
            case Command.TYPE_ENGINE_RESUME:
                content.writeByte(0x00);
                return encodeContent(command.getDeviceId(), MSG_OUTPUT_CONTROL, content);
            case Command.TYPE_ALARM_GEOFENCE:
                content.writeShort(command.getInteger(Command.KEY_RADIUS));
                return encodeContent(command.getDeviceId(), MSG_MOVEMENT_ALARM, content);
            case Command.TYPE_SET_TIMEZONE:
                int offset = command.getInteger(Command.KEY_TIMEZONE) / 60;
                content.writeBytes(String.valueOf(offset).getBytes(StandardCharsets.US_ASCII));
                return encodeContent(command.getDeviceId(), MSG_TIME_ZONE, content);
            case Command.TYPE_REBOOT_DEVICE:
                return encodeContent(command.getDeviceId(), MSG_REBOOT_GPS, content);
            default:
                Log.warning(new UnsupportedOperationException(command.getType()));
                break;
        }

        return null;
    }

}
