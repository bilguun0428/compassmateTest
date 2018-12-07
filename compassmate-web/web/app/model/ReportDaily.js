Ext.define('Compassmate.model.ReportDaily', {
    extend: 'Ext.data.Model',
    identifier: 'negative',

    fields: [{
    	name: 'deviceId',
        type: 'int'
    },{
    	name: 'resCount',
    	type: 'int'
    },{
    	name: 'startTime',
    	type: 'int'
    },{
    	name: 'endTime',
    	type: 'int'
    },{
    	name: 'resDuration',
    	type: 'int'
    }
    ]
});
