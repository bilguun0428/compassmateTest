 

Ext.define('Compassmate.store.ReportStops', {
    extend: 'Ext.data.Store',
    model: 'Compassmate.model.ReportStop',

    proxy: {
        type: 'rest',
        url: 'api/reports/stops',
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
