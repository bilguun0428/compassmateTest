package mn.compassmate.api.resource;

import mn.compassmate.Context;
import mn.compassmate.api.BaseResource;
import mn.compassmate.model.Command;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("commands")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CommandResource extends BaseResource {

    @POST
    public Response add(Command entity) throws Exception {
        Context.getPermissionsManager().checkReadonly(getUserId());
        Context.getPermissionsManager().checkDevice(getUserId(), entity.getDeviceId());
        Context.getDeviceManager().sendCommand(entity);
        return Response.ok(entity).build();
    }

}
