Ext.define('Compassmate.store.ReportFuel', {
    extend: 'Ext.data.Store',
    model: 'Compassmate.model.ReportFuel',

    proxy: {
        type: 'rest',
		url: 'api/reports/fuel',
		timeout: 600000,
		headers: {
		    'Accept': 'application/json'
		},
		listeners: {
		    exception: function(proxy, exception) {
			Compassmate.app.showError(exception);
		    }
		}
    }
});
