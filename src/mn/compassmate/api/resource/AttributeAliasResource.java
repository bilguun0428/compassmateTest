package mn.compassmate.api.resource;

import java.sql.SQLException;
import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import mn.compassmate.Context;
import mn.compassmate.api.BaseResource;
import mn.compassmate.model.AttributeAlias;

@Path("attributes/aliases")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AttributeAliasResource extends BaseResource {

    @GET
    public Collection<AttributeAlias> get(@QueryParam("deviceId") long deviceId) throws SQLException {
        if (deviceId != 0) {
            if (!Context.getPermissionsManager().isAdmin(getUserId())) {
                Context.getPermissionsManager().checkDevice(getUserId(), deviceId);
            }
            return Context.getAliasesManager().getAttributeAliases(deviceId);
        } else {
            return Context.getAliasesManager().getAllAttributeAliases(getUserId());
        }
    }

    @POST
    public Response add(AttributeAlias entity) throws SQLException {
        Context.getPermissionsManager().checkReadonly(getUserId());
        Context.getPermissionsManager().checkDeviceReadonly(getUserId());
        if (!Context.getPermissionsManager().isAdmin(getUserId())) {
            Context.getPermissionsManager().checkDevice(getUserId(), entity.getDeviceId());
        }
        Context.getAliasesManager().addAttributeAlias(entity);
        return Response.ok(entity).build();
    }

    @Path("{id}")
    @PUT
    public Response update(AttributeAlias entity) throws SQLException {
        Context.getPermissionsManager().checkReadonly(getUserId());
        Context.getPermissionsManager().checkDeviceReadonly(getUserId());
        if (!Context.getPermissionsManager().isAdmin(getUserId())) {
            AttributeAlias oldEntity = Context.getAliasesManager().getAttributeAlias(entity.getId());
            Context.getPermissionsManager().checkDevice(getUserId(), oldEntity.getDeviceId());
            Context.getPermissionsManager().checkDevice(getUserId(), entity.getDeviceId());
        }
        Context.getAliasesManager().updateAttributeAlias(entity);
        return Response.ok(entity).build();
    }

    @Path("{id}")
    @DELETE
    public Response remove(@PathParam("id") long id) throws SQLException {
        Context.getPermissionsManager().checkReadonly(getUserId());
        Context.getPermissionsManager().checkDeviceReadonly(getUserId());
        if (!Context.getPermissionsManager().isAdmin(getUserId())) {
            AttributeAlias entity = Context.getAliasesManager().getAttributeAlias(id);
            Context.getPermissionsManager().checkDevice(getUserId(), entity.getDeviceId());
        }
        Context.getAliasesManager().removeArrtibuteAlias(id);
        return Response.noContent().build();
    }

}
