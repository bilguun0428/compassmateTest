 

Ext.define('Compassmate.view.edit.AttributesController', {
    extend: 'Compassmate.view.edit.ToolbarController',
    alias: 'controller.attributes',

    requires: [
        'Compassmate.view.dialog.Attribute',
        'Compassmate.store.Attributes',
        'Compassmate.model.Attribute'
    ],

    removeTitle: Strings.stateName,

    init: function () {
        var store, propertyName, i = 0, attributes;
        store = Ext.create('Compassmate.store.Attributes');
        store.setProxy(Ext.create('Ext.data.proxy.Memory'));
        if (typeof this.getView().record.get('attributes') === 'undefined') {
            this.getView().record.set('attributes', {});
        }
        attributes = this.getView().record.get('attributes');
        for (propertyName in attributes) {
            if (attributes.hasOwnProperty(propertyName)) {
                store.add(Ext.create('Compassmate.model.Attribute', {
                    priority: i++,
                    name: propertyName,
                    value: attributes[propertyName]
                }));
            }
        }
        store.addListener('add', function (store, records) {
            var i, view;
            view = this.getView();
            for (i = 0; i < records.length; i++) {
                view.record.get('attributes')[records[i].get('name')] = records[i].get('value');
            }
            view.record.dirty = true;
        }, this);
        store.addListener('update', function (store, record, operation) {
            var view;
            view = this.getView();
            if (operation === Ext.data.Model.EDIT) {
                if (record.modified.name !== record.get('name')) {
                    delete view.record.get('attributes')[record.modified.name];
                }
                view.record.get('attributes')[record.get('name')] = record.get('value');
                view.record.dirty = true;
            }
        }, this);
        store.addListener('remove', function (store, records) {
            var i, view;
            view = this.getView();
            for (i = 0; i < records.length; i++) {
                delete view.record.get('attributes')[records[i].get('name')];
            }
            view.record.dirty = true;
        }, this);

        this.getView().setStore(store);
        if (this.getView().record instanceof Compassmate.model.Device) {
            this.getView().attributesStore = 'DeviceAttributes';
        } else if (this.getView().record instanceof Compassmate.model.Geofence) {
            this.getView().attributesStore = 'GeofenceAttributes';
        } else if (this.getView().record instanceof Compassmate.model.Group) {
            this.getView().attributesStore = 'GroupAttributes';
        } else if (this.getView().record instanceof Compassmate.model.Server) {
            this.getView().attributesStore = 'ServerAttributes';
        } else if (this.getView().record instanceof Compassmate.model.User) {
            this.getView().attributesStore = 'UserAttributes';
        }
    },

    comboConfig: {
        xtype: 'combobox',
        reference: 'nameComboField',
        name: 'name',
        fieldLabel: Strings.sharedName,
        displayField: 'name',
        valueField: 'key',
        allowBlank: false,
        queryMode: 'local',
        listeners: {
            change: 'onNameChange'
        }
    },

    initDialog: function (record) {
        var nameTextField, dialog = Ext.create('Compassmate.view.dialog.Attribute');
        if (this.getView().attributesStore) {
            this.comboConfig.store = this.getView().attributesStore;
            nameTextField = dialog.lookupReference('nameTextField');
            dialog.down('form').insert(0, this.comboConfig);
            dialog.down('form').remove(nameTextField);
        }
        dialog.down('form').loadRecord(record);
        dialog.show();
    },

    onAddClick: function () {
        var objectInstance = Ext.create('Compassmate.model.Attribute');
        objectInstance.store = this.getView().getStore();
        this.initDialog(objectInstance);
    },

    onEditClick: function () {
        this.initDialog(this.getView().getSelectionModel().getSelection()[0]);
    }
});
