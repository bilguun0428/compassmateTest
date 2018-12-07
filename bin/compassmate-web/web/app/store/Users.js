 

Ext.define('Compassmate.store.Users', {
    extend: 'Ext.data.Store',
    model: 'Compassmate.model.User',

    proxy: {
        type: 'rest',
        url: 'api/users',
        writer: {
            writeAllFields: true
        }
    }
});
