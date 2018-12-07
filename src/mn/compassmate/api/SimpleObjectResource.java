package mn.compassmate.api;

import java.sql.SQLException;
import java.util.Collection;

import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;

import mn.compassmate.Context;
import mn.compassmate.database.BaseObjectManager;
import mn.compassmate.model.BaseModel;

public class SimpleObjectResource<T extends BaseModel> extends BaseObjectResource<T> {

    public SimpleObjectResource(Class<T> baseClass) {
        super(baseClass);
    }

    @GET
    public Collection<T> get(
            @QueryParam("all") boolean all, @QueryParam("userId") long userId) throws SQLException {

        BaseObjectManager<T> manager = Context.getManager(getBaseClass());
        return manager.getItems(getSimpleManagerItems(manager, all, userId));
    }

}
