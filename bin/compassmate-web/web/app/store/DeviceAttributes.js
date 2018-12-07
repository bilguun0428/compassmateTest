 
Ext.define('Compassmate.store.DeviceAttributes', {
    extend: 'Ext.data.Store',
    model: 'Compassmate.model.KnownAttribute',

    data: [{
        key: 'web.reportColor',
        name: Strings.attributeWebReportColor,
        valueType: 'color'
    }, {
        key: 'devicePassword',
        name: Strings.attributeDevicePassword,
        valueType: 'string'
    }, {
        key: 'processing.copyAttributes',
        name: Strings.attributeProcessingCopyAttributes,
        valueType: 'string'
    }]
});
