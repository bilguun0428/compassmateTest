 
Ext.define('Compassmate.store.GeofenceAttributes', {
    extend: 'Ext.data.Store',
    model: 'Compassmate.model.KnownAttribute',
    proxy: 'memory',

    data: [{
        key: 'color',
        name: Strings.attributeColor,
        valueType: 'color'
    }]
});
