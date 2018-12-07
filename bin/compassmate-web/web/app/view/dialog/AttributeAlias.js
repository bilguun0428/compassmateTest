
Ext.define('Compassmate.view.dialog.AttributeAlias', {
    extend: 'Compassmate.view.dialog.Base',

    requires: [
        'Compassmate.view.dialog.AttributeController'
    ],

    controller: 'attribute',
    title: Strings.sharedAttributeAlias,

    items: {
        xtype: 'form',
        items: [{
            xtype: 'textfield',
            name: 'attribute',
            fieldLabel: Strings.sharedAttribute,
            allowBlank: false
        }, {
            xtype: 'textfield',
            name: 'alias',
            fieldLabel: Strings.sharedAlias,
            allowBlank: false
        }]
    },

    buttons: [{
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
