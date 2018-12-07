 Ext.define('Compassmate.store.ReportTypes', {
    extend: 'Ext.data.Store',
    fields: ['key', 'name'],

    data: [{
        key: 'route',
        name: Strings.reportRoute
    }, {
        key: 'events',
        name: Strings.reportEvents
    }, {
        key: 'fuel',
	    name: Strings.reportFuel
    }, {
        key: 'geofence',
        name: Strings.sharedGeofence
    }, {
        key: 'trips',
        name: Strings.reportTrips
    }, {
        key: 'stops',
        name: Strings.reportStops
    }, {
    	key: 'daily',
        name: Strings.reportDaily
    }, {
        key: 'summary',
        name: Strings.reportSummary
    }, {
        key: 'chart',
        name: Strings.reportChart
    }]
});
