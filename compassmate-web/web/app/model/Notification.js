 

Ext.define('Compassmate.model.Notification', {
    extend: 'Ext.data.Model',
    idProperty: 'type',

    fields: [{
        name: 'id',
        type: 'int'
    }, {
        name: 'type',
        type: 'string'
    }, {
        name: 'userId',
        type: 'int'
    }, {
        name: 'attributes'
    }, {
        name: 'web',
        type: 'bool'
    }, {
        name: 'mail',
        type: 'bool'
    }, {
        name: 'sms',
        type: 'bool'
    }]
});
