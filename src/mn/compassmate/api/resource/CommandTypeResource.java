package mn.compassmate.api.resource;

import mn.compassmate.Context;
import mn.compassmate.api.BaseResource;
import mn.compassmate.model.CommandType;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.Collection;

@Path("commandtypes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CommandTypeResource extends BaseResource {

    @GET
    public Collection<CommandType> get(@QueryParam("deviceId") long deviceId,
            @QueryParam("textChannel") boolean textChannel) {
        Context.getPermissionsManager().checkDevice(getUserId(), deviceId);
        return Context.getDeviceManager().getCommandTypes(deviceId, textChannel);
    }

}
