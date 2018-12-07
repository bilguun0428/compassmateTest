 

Ext.define('Compassmate.store.AllComputedAttributes', {
    extend: 'Ext.data.Store',
    model: 'Compassmate.model.ComputedAttribute',

    proxy: {
        type: 'rest',
        url: 'api/attributes/computed',
        extraParams: {
            all: true
        }
    }
});
