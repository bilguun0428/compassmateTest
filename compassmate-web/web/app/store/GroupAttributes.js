 
Ext.define('Compassmate.store.GroupAttributes', {
    extend: 'Ext.data.Store',
    model: 'Compassmate.model.KnownAttribute',

    data: [{
        key: 'processing.copyAttributes',
        name: Strings.attributeProcessingCopyAttributes,
        valueType: 'string'
    }]
});
