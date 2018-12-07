 

Ext.define('Compassmate.store.Devices', {
    extend: 'Ext.data.Store',
    model: 'Compassmate.model.Device',

    proxy: {
        type: 'rest',
        url: 'api/devices',
        writer: {
            writeAllFields: true
        }
    }
});
