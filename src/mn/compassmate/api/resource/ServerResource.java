package mn.compassmate.api.resource;

import mn.compassmate.Context;
import mn.compassmate.api.BaseResource;
import mn.compassmate.model.Server;

import javax.annotation.security.PermitAll;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;

@Path("server")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ServerResource extends BaseResource {

    @PermitAll
    @GET
    public Server get() throws SQLException {
        return Context.getPermissionsManager().getServer();
    }

    @PUT
    public Response update(Server entity) throws SQLException {
        Context.getPermissionsManager().checkAdmin(getUserId());
        Context.getPermissionsManager().updateServer(entity);
        return Response.ok(entity).build();
    }

}
