package mn.compassmate;

import mn.compassmate.database.ActiveDevice;
import mn.compassmate.model.Command;

import java.util.Collection;
import java.util.List;

public interface Protocol {

    String getName();

    Collection<String> getSupportedDataCommands();

    void sendDataCommand(ActiveDevice activeDevice, Command command);

    void initTrackerServers(List<TrackerServer> serverList);

    Collection<String> getSupportedTextCommands();

    void sendTextCommand(String destAddress, Command command) throws Exception;

}
