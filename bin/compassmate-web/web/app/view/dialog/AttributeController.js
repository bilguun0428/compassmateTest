 

Ext.define('Compassmate.view.dialog.AttributeController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.attribute',

    onSaveClick: function (button) {
        var dialog, store, record;
        dialog = button.up('window').down('form');
        dialog.updateRecord();
        record = dialog.getRecord();
        store = record.store;
        if (store) {
            if (record.phantom) {
                store.add(record);
            }
            store.sync({
                failure: function (batch) {
                    store.rejectChanges();
                    Compassmate.app.showError(batch.exceptions[0].getError().response);
                }
            });
        } else {
            record.save();
        }
        button.up('window').close();
    },

    onValidityChange: function (form, valid) {
        this.lookupReference('saveButton').setDisabled(!valid);
    },

    defaultFieldConfig: {
        name: 'value',
        reference: 'valueField',
        allowBlank: false,
        fieldLabel: Strings.stateValue
    },

    onNameChange: function (combobox, newValue) {
        var valueType, config, attribute, valueField = this.lookupReference('valueField');
        attribute = combobox.getStore().getById(newValue);
        if (attribute) {
            valueType = attribute.get('valueType');
            config = Ext.clone(this.defaultFieldConfig);
            if (valueType === 'number') {
                config.xtype = 'customNumberField';
                if (attribute.get('allowDecimals') !== undefined) {
                    config.allowDecimals = attribute.get('allowDecimals');
                } else {
                    config.allowDecimals = true;
                }
                config.dataType = attribute.get('dataType');
                config.maxValue = attribute.get('maxValue');
                config.minValue = attribute.get('minValue');
            } else if (valueType === 'boolean') {
                config.xtype = 'checkboxfield';
                config.inputValue = true;
                config.uncheckedValue = false;
            } else if (valueType === 'color') {
                config.xtype = 'customcolorpicker';
            } else {
                config.xtype = 'textfield';
            }
            if (valueField.getXType() !== config.xtype || valueField.convert !== config.convert) {
                this.getView().down('form').insert(this.getView().down('form').items.indexOf(valueField), config);
                this.getView().down('form').remove(valueField);
            } else if (config.xtype === 'numberfield') {
                valueField.setConfig(config);
            }
        }
    }
});
