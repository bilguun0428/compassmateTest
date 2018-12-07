 

Ext.define('Compassmate.store.AttributeAliases', {
    extend: 'Ext.data.Store',
    model: 'Compassmate.model.AttributeAlias',

    proxy: {
        type: 'rest',
        url: 'api/attributes/aliases',
        writer: {
            writeAllFields: true
        }
    }
});
