 

Ext.define('Compassmate.view.dialog.Driver', {
    extend: 'Compassmate.view.dialog.BaseEdit',

    title: Strings.sharedDevice,

    items: {
        xtype: 'form',
        items: [{
            xtype: 'fieldset',
            title: Strings.sharedRequired,
            items: [{
                xtype: 'textfield',
                name: 'name',
                fieldLabel: Strings.sharedName,
                allowBlank: false
            }, {
                xtype: 'textfield',
                name: 'uniqueId',
                fieldLabel: Strings.deviceIdentifier,
                allowBlank: false
            }]
        }]
    }
});
