package mn.compassmate;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.string.StringEncoder;
import mn.compassmate.database.ActiveDevice;
import mn.compassmate.model.Command;

import javax.xml.bind.DatatypeConverter;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class BaseProtocol implements Protocol {

    private final String name;
    private final Set<String> supportedDataCommands = new HashSet<>();
    private final Set<String> supportedTextCommands = new HashSet<>();

    private StringProtocolEncoder textCommandEncoder = null;

    public BaseProtocol(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setSupportedDataCommands(String... commands) {
        supportedDataCommands.addAll(Arrays.asList(commands));
    }

    public void setSupportedTextCommands(String... commands) {
        supportedTextCommands.addAll(Arrays.asList(commands));
    }

    public void setSupportedCommands(String... commands) {
        supportedDataCommands.addAll(Arrays.asList(commands));
        supportedTextCommands.addAll(Arrays.asList(commands));
    }

    @Override
    public Collection<String> getSupportedDataCommands() {
        Set<String> commands = new HashSet<>(supportedDataCommands);
        commands.add(Command.TYPE_CUSTOM);
        return commands;
    }

    @Override
    public Collection<String> getSupportedTextCommands() {
        Set<String> commands = new HashSet<>(supportedTextCommands);
        commands.add(Command.TYPE_CUSTOM);
        return commands;
    }

    @Override
    public void sendDataCommand(ActiveDevice activeDevice, Command command) {
        if (supportedDataCommands.contains(command.getType())) {
            activeDevice.write(command);
        } else if (command.getType().equals(Command.TYPE_CUSTOM)) {
            String data = command.getString(Command.KEY_DATA);
            if (activeDevice.getChannel().getPipeline().get(StringEncoder.class) != null) {
                activeDevice.write(data);
            } else {
                activeDevice.write(ChannelBuffers.wrappedBuffer(DatatypeConverter.parseHexBinary(data)));
            }
        } else {
            throw new RuntimeException("Command " + command.getType() + " is not supported in protocol " + getName());
        }
    }

    public void setTextCommandEncoder(StringProtocolEncoder textCommandEncoder) {
        this.textCommandEncoder = textCommandEncoder;
    }

    @Override
    public void sendTextCommand(String destAddress, Command command) throws Exception {
        if (Context.getSmppManager() != null) {
            if (command.getType().equals(Command.TYPE_CUSTOM)) {
                Context.getSmppManager().sendMessageSync(destAddress, command.getString(Command.KEY_DATA), true);
            } else if (supportedTextCommands.contains(command.getType()) && textCommandEncoder != null) {
                Context.getSmppManager().sendMessageSync(destAddress,
                        (String) textCommandEncoder.encodeCommand(command), true);
            } else {
                throw new RuntimeException(
                        "Command " + command.getType() + " is not supported in protocol " + getName());
            }
        } else {
            throw new RuntimeException("SMPP client is not enabled");
        }
    }

}
