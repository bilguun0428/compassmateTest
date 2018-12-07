 

Ext.define('Compassmate.store.ReportRoute', {
    extend: 'Ext.data.Store',
    model: 'Compassmate.model.Position',

    proxy: {
        type: 'rest',
        url: 'api/reports/route',
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
