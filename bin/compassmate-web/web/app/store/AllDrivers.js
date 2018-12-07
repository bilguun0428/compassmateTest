 

Ext.define('Compassmate.store.AllDrivers', {
    extend: 'Ext.data.Store',
    model: 'Compassmate.model.Driver',

    proxy: {
        type: 'rest',
        url: 'api/drivers',
        extraParams: {
            all: true
        }
    }
});
