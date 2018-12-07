 

Ext.define('Compassmate.view.CustomTimeField', {
    extend: 'Ext.form.field.Time',
    xtype: 'customTimeField',

    constructor: function (config) {
        if (Compassmate.app.getPreference('twelveHourFormat', false)) {
            config.format = Compassmate.Style.timeFormat12;
        } else {
            config.format = Compassmate.Style.timeFormat24;
        }
        this.callParent(arguments);
    }
});
