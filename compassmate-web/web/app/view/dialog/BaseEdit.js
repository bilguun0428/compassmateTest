 

Ext.define('Compassmate.view.dialog.BaseEdit', {
    extend: 'Compassmate.view.dialog.Base',

    requires: [
        'Compassmate.view.dialog.BaseEditController'
    ],

    controller: 'baseEdit',

    buttons: [{
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
