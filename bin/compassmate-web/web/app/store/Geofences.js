 

Ext.define('Compassmate.store.Geofences', {
    extend: 'Ext.data.Store',
    model: 'Compassmate.model.Geofence',

    proxy: {
        type: 'rest',
        url: 'api/geofences',
        writer: {
            writeAllFields: true
        }
    }
});
