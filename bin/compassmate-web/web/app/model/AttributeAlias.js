 

Ext.define('Compassmate.model.AttributeAlias', {
    extend: 'Ext.data.Model',
    identifier: 'negative',

    fields: [{
        name: 'id',
        type: 'int'
    }, {
        name: 'deviceId',
        type: 'int'
    }, {
        name: 'attribute',
        type: 'string'
    }, {
        name: 'alias',
        type: 'string'
    }]
});
