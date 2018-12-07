 

Ext.define('Compassmate.model.Command', {
    extend: 'Ext.data.Model',
    identifier: 'negative',

    fields: [{
        name: 'deviceId',
        type: 'int'
    }, {
        name: 'type',
        type: 'string'
    }, {
        name: 'textChannel',
        type: 'boolean'
    }, {
        name: 'attributes'
    }]
});
