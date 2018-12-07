 

Ext.define('Compassmate.store.ReportChartTypes', {
    extend: 'Ext.data.Store',
    fields: ['key', 'name'],

    data: [{
        key: 'speed',
        name: Strings.positionSpeed
    }, {
        key: 'accuracy',
        name: Strings.positionAccuracy
    }]
});
