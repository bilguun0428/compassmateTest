 

Ext.define('Compassmate.model.ReportSummary', {
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
    }, /*{
        name: 'averageSpeed',
        type: 'float',
        convert: Compassmate.AttributeFormatter.getConverter('speed')
    }, */{
        name: 'distance',
        type: 'float',
        convert: Compassmate.AttributeFormatter.getConverter('distance')
    }, {
        name: 'engineHours',
        type: 'int'
    },
    {
    	name: 'overSpeedDistance',
    	type: 'float',
    	convert: Compassmate.AttributeFormatter.getConverter('overSpeedDistance')
    },
    {
    	name: 'activeStopTime',
    	type: 'int',


    },
    {
    	name: 'activeMoveTime',
    	type: 'int',


    },
    {
    	name:'resCount',
    	type: 'int',
    }]

});
