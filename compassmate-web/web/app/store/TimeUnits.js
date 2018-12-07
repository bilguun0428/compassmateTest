 

Ext.define('Compassmate.store.TimeUnits', {
    extend: 'Ext.data.Store',
    fields: ['name', 'factor'],

    data: [{
        name: Strings.sharedSecond,
        factor: 1
    }, {
        name: Strings.sharedMinute,
        factor: 60
    }, {
        name: Strings.sharedHour,
        factor: 3600
    }]
});
