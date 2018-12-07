Ext.define('Compassmate.store.ReportDaily', {
    extend: 'Ext.data.Store',
    model: 'Compassmate.model.ReportDaily',

    proxy: {
        type: 'rest',
        url: 'api/reports/daily',
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
