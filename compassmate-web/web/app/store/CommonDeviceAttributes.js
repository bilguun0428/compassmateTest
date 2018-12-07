 
Ext.define('Compassmate.store.CommonDeviceAttributes', {
    extend: 'Ext.data.Store',
    model: 'Compassmate.model.KnownAttribute',

    data: [{
        key: 'speedLimit',
        name: Strings.attributeSpeedLimit,
        valueType: 'number',
        dataType: 'speed'
    }, {
        key: 'report.ignoreOdometer',
        name: Strings.attributeReportIgnoreOdometer,
        valueType: 'boolean'
    }, {
        key: 'maintenance.start',
        name: Strings.attributeMaintenanceStart,
        valueType: 'number',
        dataType: 'distance'
    }, {
        key: 'maintenance.interval',
        name: Strings.attributeMaintenanceInterval,
        valueType: 'number',
        dataType: 'distance'
    }]
});
