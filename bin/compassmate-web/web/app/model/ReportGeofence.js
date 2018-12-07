  
Ext.define('Compassmate.model.ReportGeofence', {
    extend: 'Ext.data.Model',
    identifier: 'negative',

    fields: [{
        name: 'geofenceName',
        type: 'string'
    }, {
        name: 'groupName',
        type: 'string'
    }, {
        name: 'deviceName',
        type: 'string'
    }, {
        name: 'entryDate',
        type: 'date',
        dateFormat: 'c'
    }, {
        name: 'exitDate',
        type: 'date',
        dateFormat: 'c'
    }, {
        name: 'distance',
        type: 'float',
        convert: Compassmate.AttributeFormatter.getConverter('distance')
    }, {
        name: 'duration',
        type: 'int'
    }, {
        name: 'spentFuel',
        type: 'float'
    }]
});
