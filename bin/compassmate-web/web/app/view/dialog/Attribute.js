 

Ext.define('Compassmate.view.dialog.Attribute', {
    extend: 'Compassmate.view.dialog.Base',

    requires: [
        'Compassmate.view.dialog.AttributeController',
        'Compassmate.view.ColorPicker',
        'Compassmate.view.CustomNumberField'
    ],

    controller: 'attribute',
    title: Strings.sharedAttribute,

    items: {
        xtype: 'form',
        listeners: {
            validitychange: 'onValidityChange'
        },
        items: [{
            xtype: 'textfield',
            reference: 'nameTextField',
            name: 'name',
            allowBlank: false,
            fieldLabel: Strings.sharedName
        }, {
            xtype: 'textfield',
            name: 'value',
            reference: 'valueField',
            allowBlank: false,
            fieldLabel: Strings.stateValue
        }]
    },

    buttons: [{
        glyph: 'xf00c@FontAwesome',
        reference: 'saveButton',
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
