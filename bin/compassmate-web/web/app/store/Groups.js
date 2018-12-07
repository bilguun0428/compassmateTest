 

Ext.define('Compassmate.store.Groups', {
    extend: 'Ext.data.Store',
    model: 'Compassmate.model.Group',

    proxy: {
        type: 'rest',
        url: 'api/groups',
        writer: {
            writeAllFields: true
        }
    }
});
