 

Ext.define('Compassmate.view.dialog.ComputedAttributeController', {
    extend: 'Compassmate.view.dialog.BaseEditController',
    alias: 'controller.computedAttribute',

    requires: [
        'Compassmate.view.dialog.SelectDevice'
    ],

    onAttributeChange: function (combobox, newValue) {
        var attribute = Ext.getStore('PositionAttributes').getById(newValue);
        if (attribute) {
            this.getView().lookupReference('typeComboField').setValue(attribute.get('valueType'));
            this.getView().lookupReference('typeComboField').setReadOnly(true);
        } else {
            this.getView().lookupReference('typeComboField').setReadOnly(false);
        }
    },

    onCheckClick: function (button) {
        var dialog, form;
        dialog = Ext.create('Compassmate.view.dialog.SelectDevice');
        form = button.up('window').down('form');
        form.updateRecord();
        dialog.record = form.getRecord();
        dialog.show();
    }
});
