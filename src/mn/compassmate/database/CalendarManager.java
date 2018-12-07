package mn.compassmate.database;

import mn.compassmate.model.Calendar;

public class CalendarManager extends SimpleObjectManager<Calendar> {

    public CalendarManager(DataManager dataManager) {
        super(dataManager, Calendar.class);
    }

}
