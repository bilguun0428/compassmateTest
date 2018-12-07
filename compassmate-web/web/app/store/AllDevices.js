 

Ext.define('Compassmate.store.AllDevices', {
    extend: 'Ext.data.Store',
    model: 'Compassmate.model.Device',

    proxy: {
        type: 'rest',
        url: 'api/devices',
        extraParams: {
            all: true
        }
    }
});
