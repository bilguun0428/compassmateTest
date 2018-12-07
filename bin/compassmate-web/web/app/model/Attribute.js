 

Ext.define('Compassmate.model.Attribute', {
    extend: 'Ext.data.Model',

    fields: [{
        name: 'priority',
        type: 'int'
    }, {
        name: 'name',
        type: 'string'
    }, {
        name: 'value',
        type: 'string'
    }, {
        name: 'attribute',
        type: 'string'
    }]
});
