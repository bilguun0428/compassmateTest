 

Ext.define('Compassmate.store.ReportSummary', {
    extend: 'Ext.data.Store',
    model: 'Compassmate.model.ReportSummary',

    proxy: {
        type: 'rest',
        url: 'api/reports/summary',
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
