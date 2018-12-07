
Ext.define('Compassmate.view.edit.AttributeAliasesController', {
    extend: 'Compassmate.view.edit.ToolbarController',
    alias: 'controller.attributeAliases',

    requires: [
        'Compassmate.view.dialog.AttributeAlias',
        'Compassmate.model.AttributeAlias'
    ],

    objectModel: 'Compassmate.model.AttributeAlias',
    objectDialog: 'Compassmate.view.dialog.AttributeAlias',
    removeTitle: Strings.sharedAttributeAlias,

    init: function () {
        var manager = Compassmate.app.getUser().get('admin') || Compassmate.app.getUser().get('userLimit') > 0;
        this.lookupReference('deviceField').setStore(manager ? 'AllDevices' : 'Devices');
        this.lookupReference('toolbarAddButton').setDisabled(true);
        this.lookupReference('toolbarEditButton').setDisabled(true);
        this.lookupReference('toolbarRemoveButton').setDisabled(true);
        this.getView().setStore(Ext.create('Ext.data.ChainedStore', {
            storeId: 'EditorAttributeAliases',
            source: 'AttributeAliases'
        }));
        this.getView().getStore().filter('deviceId', 0);
    },

    onAddClick: function () {
        var attributeAlias, dialog, deviceId;
        attributeAlias = Ext.create('Compassmate.model.AttributeAlias');
        attributeAlias.store = Ext.getStore('AttributeAliases');
        deviceId = this.lookupReference('deviceField').getValue();
        attributeAlias.set('deviceId', deviceId);
        dialog = Ext.create('Compassmate.view.dialog.AttributeAlias');
        dialog.down('form').loadRecord(attributeAlias);
        dialog.show();
    },

    onSelectionChange: function (selection, selected) {
        var disabled = !this.lookupReference('deviceField').getValue();
        this.lookupReference('toolbarAddButton').setDisabled(disabled);
        disabled = !selected || selected.length === 0 || !this.lookupReference('deviceField').getValue();
        this.lookupReference('toolbarEditButton').setDisabled(disabled);
        this.lookupReference('toolbarRemoveButton').setDisabled(disabled);
    },

    onDeviceChange: function (combobox, value) {
        var manager = Compassmate.app.getUser().get('admin') || Compassmate.app.getUser().get('userLimit') > 0;
        this.onSelectionChange();
        if (value !== null) {
            this.getView().getStore().filter('deviceId', value);
            if (manager && this.getView().getStore().getCount() === 0) {
                Ext.getStore('AttributeAliases').getProxy().setExtraParam('deviceId', value);
                Ext.getStore('AttributeAliases').load({
                    addRecords: true
                });
            }
        } else {
            this.getView().getStore().filter('deviceId', 0);
        }
    }
});
