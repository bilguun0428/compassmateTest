 

Ext.define('Compassmate.model.ReportTrip', {
    extend: 'Ext.data.Model',
    identifier: 'negative',

    fields: [{
        name: 'deviceId',
        type: 'int'
    }, {
        name: 'deviceName',
        type: 'string'
    }, {
        name: 'maxSpeed',
        type: 'float',
        convert: Compassmate.AttributeFormatter.getConverter('speed')
    }, {
        name: 'averageSpeed',
        type: 'float',
        convert: Compassmate.AttributeFormatter.getConverter('speed')
    }, {
        name: 'distance',
        type: 'float',
        convert: Compassmate.AttributeFormatter.getConverter('distance')
    }, {
        name: 'duration',
        type: 'int'
    }, {
        name: 'startTime',
        type: 'date',
        dateFormat: 'c'
    }, {
        name: 'startAddress',
        type: 'string'
    }, {
        name: 'endTime',
        type: 'date',
        dateFormat: 'c'
    }, {
        name: 'endAddress',
        type: 'string'
    }, {
        name: 'driverUniqueId',
        type: 'string'
    }, {
        name: 'driverName',
        type: 'string'
    }]
});
