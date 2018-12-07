
Ext.define('Compassmate.view.dialog.ReportConfigController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.reportConfig',

    requires: [
        'Compassmate.store.ReportEventTypes',
        'Compassmate.store.AllNotifications'
    ],

    onSaveClick: function (button) {
        var eventType, callingPanel;
        callingPanel = this.getView().callingPanel;

        callingPanel.deviceId = this.lookupReference('deviceField').getValue();
        callingPanel.groupId = this.lookupReference('groupField').getValue();
        callingPanel.geofenceId = this.lookupReference('geofenceField').getValue();
        eventType = this.lookupReference('eventTypeField').getValue();
        if (eventType.indexOf(Compassmate.store.ReportEventTypes.allEvents) > -1) {
            eventType = [Compassmate.store.ReportEventTypes.allEvents];
        } else if (eventType.length === this.lookupReference('eventTypeField').getStore().getCount() - 1) {
            eventType = [Compassmate.store.ReportEventTypes.allEvents];
        }
        callingPanel.eventType = eventType;
        callingPanel.chartType = this.lookupReference('chartTypeField').getValue();
        callingPanel.showMarkers = this.lookupReference('showMarkersField').getValue();
        callingPanel.fromDate = this.lookupReference('fromDateField').getValue();
        callingPanel.fromTime = this.lookupReference('fromTimeField').getValue();
        callingPanel.toDate = this.lookupReference('toDateField').getValue();
        callingPanel.toTime = this.lookupReference('toTimeField').getValue();
        callingPanel.updateButtons();
        button.up('window').close();
    }
});
