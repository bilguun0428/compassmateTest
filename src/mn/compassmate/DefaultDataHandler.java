package mn.compassmate;

import mn.compassmate.helper.Log;
import mn.compassmate.model.Position;

public class DefaultDataHandler extends BaseDataHandler {

    @Override
    protected Position handlePosition(Position position) {

        try {
            Context.getDataManager().addPosition(position);
        } catch (Exception error) {
            Log.warning(error);
        }

        return position;
    }

}
