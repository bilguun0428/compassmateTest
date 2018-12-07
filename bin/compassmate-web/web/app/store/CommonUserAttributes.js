 
Ext.define('Compassmate.store.CommonUserAttributes', {
    extend: 'Ext.data.Store',
    model: 'Compassmate.model.KnownAttribute',

    data: [{
        key: 'web.liveRouteLength',
        name: Strings.attributeWebLiveRouteLength,
        valueType: 'number',
        allowDecimals: false
    }, {
        key: 'web.selectZoom',
        name: Strings.attributeWebSelectZoom,
        valueType: 'number',
        allowDecimals: false,
        minValue: Compassmate.Style.mapDefaultZoom,
        maxValue: Compassmate.Style.mapMaxZoom
    }, {
        key: 'web.maxZoom',
        name: Strings.attributeWebMaxZoom,
        valueType: 'number',
        allowDecimals: false,
        minValue: Compassmate.Style.mapDefaultZoom,
        maxValue: Compassmate.Style.mapMaxZoom
    }, {
        key: 'ui.disableReport',
        name: Strings.attributeUiDisableReport,
        valueType: 'boolean'
    }, {
        key: 'ui.disableVehicleFetures',
        name: Strings.attributeUiDisableVehicleFetures,
        valueType: 'boolean'
    }, {
        key: 'ui.disableDrivers',
        name: Strings.attributeUiDisableDrivers,
        valueType: 'boolean'
    }, {
        key: 'ui.disableComputedAttributes',
        name: Strings.attributeUiDisableComputedAttributes,
        valueType: 'boolean'
    }, {
        key: 'ui.disableCalendars',
        name: Strings.attributeUiDisableCalendars,
        valueType: 'boolean'
    }]
});
