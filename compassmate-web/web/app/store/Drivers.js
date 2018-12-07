 

Ext.define('Compassmate.store.Drivers', {
    extend: 'Ext.data.Store',
    model: 'Compassmate.model.Driver',

    proxy: {
        type: 'rest',
        url: 'api/drivers',
        writer: {
            writeAllFields: true
        }
    }
});
