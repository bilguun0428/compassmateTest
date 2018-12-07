 

Ext.define('Compassmate.store.ReportEvents', {
    extend: 'Ext.data.Store',
    model: 'Compassmate.model.Event',

    proxy: {
        type: 'rest',
        url: 'api/reports/events',
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
