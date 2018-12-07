package mn.compassmate;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
import mn.compassmate.helper.Log;
import mn.compassmate.model.Command;
import mn.compassmate.model.Device;

public abstract class BaseProtocolEncoder extends OneToOneEncoder {

    protected String getUniqueId(long deviceId) {
        return Context.getIdentityManager().getById(deviceId).getUniqueId();
    }

    protected void initDevicePassword(Command command, String defaultPassword) {
        if (!command.getAttributes().containsKey(Command.KEY_DEVICE_PASSWORD)) {
            Device device = Context.getIdentityManager().getById(command.getDeviceId());
            String password = device.getString(Command.KEY_DEVICE_PASSWORD);
            if (password != null) {
                command.set(Command.KEY_DEVICE_PASSWORD, password);
            } else {
                command.set(Command.KEY_DEVICE_PASSWORD, defaultPassword);
            }
        }
    }

    @Override
    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {

        if (msg instanceof Command) {
            Command command = (Command) msg;
            Object encodedCommand = encodeCommand(command);

            // Log command
            StringBuilder s = new StringBuilder();
            s.append(String.format("[%08X] ", channel.getId()));
            s.append("id: ").append(getUniqueId(command.getDeviceId())).append(", ");
            s.append("command type: ").append(command.getType()).append(" ");
            if (encodedCommand != null) {
                s.append("sent");
            } else {
                s.append("not sent");
            }
            Log.info(s.toString());

            return encodedCommand;
        }

        return msg;
    }

    protected abstract Object encodeCommand(Command command);

}
