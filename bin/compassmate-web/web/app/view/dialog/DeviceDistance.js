
Ext.define('Compassmate.view.dialog.DeviceDistance', {
    extend: 'Compassmate.view.dialog.Base',

    requires: [
        'Compassmate.view.dialog.DeviceDistanceController'
    ],

    controller: 'deviceDistance',
    title: Strings.sharedDeviceDistance,

    items: [{
        xtype: 'combobox',
        reference: 'deviceId',
        fieldLabel: Strings.sharedDevice,
        store: 'AllDevices',
        displayField: 'name',
        valueField: 'id',
        editable: false,
        listeners: {
            change: 'onDeviceChange'
        }
    }, {
        xtype: 'customNumberField',
        dataType: 'distance',
        reference: 'totalDistance',
        fieldLabel: Strings.deviceTotalDistance
    }],

    buttons: [{
        disabled: true,
        reference: 'setButton',
        glyph: 'xf00c@FontAwesome',
        tooltip: Strings.sharedSet,
        tooltipType: 'title',
        minWidth: 0,
        handler: 'onSetClick'
    }, {
        glyph: 'xf00d@FontAwesome',
        tooltip: Strings.sharedCancel,
        tooltipType: 'title',
        minWidth: 0,
        handler: 'closeView'
    }]
});
