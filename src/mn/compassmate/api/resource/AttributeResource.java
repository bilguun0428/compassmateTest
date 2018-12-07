package mn.compassmate.api.resource;

import java.sql.SQLException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import mn.compassmate.Context;
import mn.compassmate.api.ExtendedObjectResource;
import mn.compassmate.model.Attribute;
import mn.compassmate.model.Position;
import mn.compassmate.processing.ComputedAttributesHandler;

@Path("attributes/computed")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AttributeResource extends ExtendedObjectResource<Attribute> {

    public AttributeResource() {
        super(Attribute.class);
    }

    @POST
    @Path("test")
    public Response test(@QueryParam("deviceId") long deviceId, Attribute entity) throws SQLException {
        Context.getPermissionsManager().checkReadonly(getUserId());
        Context.getPermissionsManager().checkDevice(getUserId(), deviceId);
        Position last = Context.getIdentityManager().getLastPosition(deviceId);
        if (last != null) {
            Object result = new ComputedAttributesHandler().computeAttribute(entity, last);
            if (result != null) {
                switch (entity.getType()) {
                    case "number":
                        return Response.ok((Number) result).build();
                    case "boolean":
                        return Response.ok((Boolean) result).build();
                    default:
                        return Response.ok(result.toString()).build();
                }
            } else {
                return Response.noContent().build();
            }
        } else {
            throw new IllegalArgumentException("Device has no last position");
        }
    }

}
