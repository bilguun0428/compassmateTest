 

Ext.define('Compassmate.store.ReportTrips', {
    extend: 'Ext.data.Store',
    model: 'Compassmate.model.ReportTrip',

    proxy: {
        type: 'rest',
        url: 'api/reports/trips',
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
