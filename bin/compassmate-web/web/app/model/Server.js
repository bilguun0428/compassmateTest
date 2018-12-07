 

Ext.define('Compassmate.model.Server', {
    extend: 'Ext.data.Model',
    identifier: 'negative',

    fields: [{
        name: 'id',
        type: 'int'
    }, {
        name: 'registration',
        type: 'boolean'
    }, {
        name: 'readonly',
        type: 'boolean'
    }, {
        name: 'deviceReadonly',
        type: 'boolean'
    }, {
        name: 'map',
        type: 'string'
    }, {
        name: 'bingKey',
        type: 'string'
    }, {
        name: 'mapUrl',
        type: 'string'
    }, {
        name: 'distanceUnit',
        type: 'string'
    }, {
        name: 'speedUnit',
        type: 'string'
    }, {
        name: 'latitude',
        type: 'float'
    }, {
        name: 'longitude',
        type: 'float'
    }, {
        name: 'zoom',
        type: 'int'
    }, {
        name: 'twelveHourFormat',
        type: 'boolean'
    }, {
        name: 'forceSettings',
        type: 'boolean'
    }, {
        name: 'coordinateFormat',
        type: 'string'
    }, {
        name: 'timezone',
        type: 'string'
    }, {
        name: 'attributes'
    }],

    proxy: {
        type: 'ajax',
        url: 'api/server',
        actionMethods: {
            update: 'PUT'
        },
        writer: {
            type: 'json',
            writeAllFields: true
        }
    }
});
