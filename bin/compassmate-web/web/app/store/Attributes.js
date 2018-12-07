 

Ext.define('Compassmate.store.Attributes', {
    extend: 'Ext.data.Store',
    model: 'Compassmate.model.Attribute',

    sorters: [{
        property: 'priority'
    }]
});
