
Ext.define('Compassmate.view.edit.CalendarsController', {
    extend: 'Compassmate.view.edit.ToolbarController',
    alias: 'controller.calendars',

    requires: [
        'Compassmate.view.dialog.Calendar',
        'Compassmate.model.Calendar'
    ],

    objectModel: 'Compassmate.model.Calendar',
    objectDialog: 'Compassmate.view.dialog.Calendar',
    removeTitle: Strings.sharedCalendar

});
