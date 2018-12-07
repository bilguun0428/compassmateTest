package mn.compassmate.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import mn.compassmate.BaseProtocolEncoder;
import mn.compassmate.helper.Checksum;
import mn.compassmate.helper.Log;
import mn.compassmate.model.Command;

import java.nio.charset.StandardCharsets;

public class RuptelaProtocolEncoder extends BaseProtocolEncoder {

    private ChannelBuffer encodeContent(int type, ChannelBuffer content) {

        ChannelBuffer buf = ChannelBuffers.dynamicBuffer();

        buf.writeShort(1 + content.readableBytes());
        buf.writeByte(100 + type);
        buf.writeBytes(content);
        buf.writeShort(Checksum.crc16(Checksum.CRC16_KERMIT, buf.toByteBuffer(2, buf.writerIndex() - 2)));

        return buf;
    }

    @Override
    protected Object encodeCommand(Command command) {

        ChannelBuffer content = ChannelBuffers.dynamicBuffer();

        switch (command.getType()) {
            case Command.TYPE_CUSTOM:
                content.writeBytes(command.getString(Command.KEY_DATA).getBytes(StandardCharsets.US_ASCII));
                return encodeContent(RuptelaProtocolDecoder.MSG_SMS_VIA_GPRS, content);
            case Command.TYPE_CONFIGURATION:
                content.writeBytes((command.getString(Command.KEY_DATA) + "\r\n").getBytes(StandardCharsets.US_ASCII));
                return encodeContent(RuptelaProtocolDecoder.MSG_DEVICE_CONFIGURATION, content);
            case Command.TYPE_GET_VERSION:
                return encodeContent(RuptelaProtocolDecoder.MSG_DEVICE_VERSION, content);
            case Command.TYPE_FIRMWARE_UPDATE:
                content.writeBytes("|FU_STRT*\r\n".getBytes(StandardCharsets.US_ASCII));
                return encodeContent(RuptelaProtocolDecoder.MSG_FIRMWARE_UPDATE, content);
            case Command.TYPE_OUTPUT_CONTROL:
                content.writeInt(command.getInteger(Command.KEY_INDEX));
                content.writeInt(Integer.parseInt(command.getString(Command.KEY_DATA)));
                return encodeContent(RuptelaProtocolDecoder.MSG_SET_IO, content);
            case Command.TYPE_SET_CONNECTION:
                String c = command.getString(Command.KEY_SERVER) + "," + command.getInteger(Command.KEY_PORT) + ",TCP";
                content.writeBytes(c.getBytes(StandardCharsets.US_ASCII));
                return encodeContent(RuptelaProtocolDecoder.MSG_SET_CONNECTION, content);
            case Command.TYPE_SET_ODOMETER:
                content.writeInt(Integer.parseInt(command.getString(Command.KEY_DATA)));
                return encodeContent(RuptelaProtocolDecoder.MSG_SET_ODOMETER, content);
            default:
                Log.warning(new UnsupportedOperationException(command.getType()));
                break;
        }

        return null;
    }

}
