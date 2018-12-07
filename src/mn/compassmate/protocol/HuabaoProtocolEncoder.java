package mn.compassmate.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import mn.compassmate.BaseProtocolEncoder;
import mn.compassmate.helper.Log;
import mn.compassmate.model.Command;

import javax.xml.bind.DatatypeConverter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HuabaoProtocolEncoder extends BaseProtocolEncoder {

    @Override
    protected Object encodeCommand(Command command) {

        ChannelBuffer id =  ChannelBuffers.wrappedBuffer(
                DatatypeConverter.parseHexBinary(getUniqueId(command.getDeviceId())));

        ChannelBuffer data = ChannelBuffers.dynamicBuffer();
        byte[] time = DatatypeConverter.parseHexBinary(new SimpleDateFormat("yyMMddHHmmss").format(new Date()));

        switch (command.getType()) {
            case Command.TYPE_ENGINE_STOP:
                data.writeByte(0x01);
                data.writeBytes(time);
                return HuabaoProtocolDecoder.formatMessage(HuabaoProtocolDecoder.MSG_OIL_CONTROL, id, data);
            case Command.TYPE_ENGINE_RESUME:
                data.writeByte(0x00);
                data.writeBytes(time);
                return HuabaoProtocolDecoder.formatMessage(HuabaoProtocolDecoder.MSG_OIL_CONTROL, id, data);
            default:
                Log.warning(new UnsupportedOperationException(command.getType()));
                return null;
        }
    }

}
