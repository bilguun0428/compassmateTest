 Ext.define('Compassmate.store.ReportGeofence', {
    extend: 'Ext.data.Store',
    model: 'Compassmate.model.ReportGeofence',

    proxy: {
        type: 'rest',
        url: 'api/reports/geofence',
        timeout: 600000,
        headers: {
            'Accept': 'application/json'
        },
        listeners: {
            exception: function (proxy, exception) {
                Compassmate.app.showError(exception);
            }
        }
    }
});
