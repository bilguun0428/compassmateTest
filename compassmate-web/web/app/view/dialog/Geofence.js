 

Ext.define('Compassmate.view.dialog.Geofence', {
    extend: 'Compassmate.view.dialog.BaseEdit',

    requires: [
        'Compassmate.view.dialog.GeofenceController'
    ],

    controller: 'geofence',
    title: Strings.sharedGeofence,

    items: {
        xtype: 'form',
        items: [{
            xtype: 'fieldset',
            title: Strings.sharedRequired,
            items: [{
                xtype: 'textfield',
                name: 'name',
                fieldLabel: Strings.sharedName
            }]
        }, {
            xtype: 'fieldset',
            title: Strings.sharedExtra,
            collapsible: true,
            collapsed: true,
            items: [{
                xtype: 'textfield',
                name: 'description',
                fieldLabel: Strings.sharedDescription
            }, {
                xtype: 'combobox',
                reference: 'calendarCombo',
                name: 'calendarId',
                store: 'Calendars',
                queryMode: 'local',
                displayField: 'name',
                valueField: 'id',
                fieldLabel: Strings.sharedCalendar
            }, {
                xtype: 'hiddenfield',
                name: 'area',
                allowBlank: false,
                reference: 'areaField'
            }]
        }]
    },

    buttons: [{
        text: Strings.sharedArea,
        glyph: 'xf21d@FontAwesome',
        handler: 'onAreaClick',
        width: 100,
    }, {
        text: Strings.sharedAttributes,
        handler: 'showAttributesView'
    }, {
        xtype: 'tbfill'
    }, {
        glyph: 'xf00c@FontAwesome',
        tooltip: Strings.sharedSave,
        tooltipType: 'title',
        minWidth: 0,
        handler: 'onSaveClick'
    }, {
        glyph: 'xf00d@FontAwesome',
        tooltip: Strings.sharedCancel,
        tooltipType: 'title',
        minWidth: 0,
        handler: 'closeView'
    }]
});
