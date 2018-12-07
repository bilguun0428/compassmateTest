 

Ext.define('Compassmate.store.ComputedAttributes', {
    extend: 'Ext.data.Store',
    model: 'Compassmate.model.ComputedAttribute',

    proxy: {
        type: 'rest',
        url: 'api/attributes/computed',
        writer: {
            writeAllFields: true
        }
    }
});
