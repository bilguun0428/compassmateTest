 

Ext.define('Compassmate.store.AllGeofences', {
    extend: 'Ext.data.Store',
    model: 'Compassmate.model.Geofence',

    proxy: {
        type: 'rest',
        url: 'api/geofences',
        extraParams: {
            all: true
        }
    }
});
