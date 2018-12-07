 
Ext.define('Compassmate.view.CustomNumberField', {
    extend: 'Ext.form.field.Number',
    xtype: 'customNumberField',

    beforeEl: '<div style="width:100%;display:inline-table;">',
    unitEl: '<div style="display:table-cell;padding-left:10px;vertical-align:middle;">',

    constructor: function (config) {
        var unit;
        if (config.dataType === 'speed') {
            unit = Compassmate.app.getPreference('speedUnit', 'kn');
            config.beforeSubTpl = this.beforeEl;
            config.afterSubTpl = this.unitEl + Ext.getStore('SpeedUnits').findRecord('key', unit).get('name') + '</div></div>';
            config.rawToValue = function (rawValue) {
                return Ext.getStore('SpeedUnits').convertValue(rawValue, Compassmate.app.getPreference('speedUnit', 'kn'), true);
            };
            config.valueToRaw = function (value) {
                return Ext.getStore('SpeedUnits').convertValue(value, Compassmate.app.getPreference('speedUnit', 'kn'));
            };
        } else if (config.dataType === 'distance') {
            config.beforeSubTpl = this.beforeEl;
            unit = Compassmate.app.getPreference('distanceUnit', 'km');
            config.afterSubTpl = this.unitEl + Ext.getStore('DistanceUnits').findRecord('key', unit).get('name') + '</div></div>';
            config.rawToValue = function (rawValue) {
                return Ext.getStore('DistanceUnits').convertValue(rawValue, Compassmate.app.getPreference('distanceUnit', 'km'), true);
            };
            config.valueToRaw = function (value) {
                return Ext.getStore('DistanceUnits').convertValue(value, Compassmate.app.getPreference('distanceUnit', 'km'));
            };
        }
        this.callParent(arguments);
    }
});
