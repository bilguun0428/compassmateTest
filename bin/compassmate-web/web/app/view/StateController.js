 

Ext.define('Compassmate.view.StateController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.state',

    requires: [
        'Compassmate.AttributeFormatter',
        'Compassmate.model.Attribute',
        'Compassmate.model.AttributeAlias',
        'Compassmate.model.Position',
        'Compassmate.view.dialog.AttributeAlias'
    ],

    config: {
        listen: {
            controller: {
                '*': {
                    selectdevice: 'selectDevice',
                    selectreport: 'selectReport',
                    updatealiases: 'updateAliases',
                    deselectfeature: 'deselectFeature'
                }
            },
            store: {
                '#LatestPositions': {
                    add: 'updateLatest',
                    update: 'updateLatest'
                },
                '#ReportRoute': {
                    clear: 'clearReport'
                },
                '#AttributeAliases': {
                    add: 'updateAliases',
                    update: 'updateAliases'
                }
            }
        }
    },

    init: function () {
        var visible = !Compassmate.app.getUser().get('deviceReadonly') && !Compassmate.app.getPreference('readonly', false);
        this.lookupReference('aliasEditButton').setVisible(visible);
        this.aliasesStore = Ext.getStore('AttributeAliases');
    },

    keys: (function () {
        var i, list, result;
        result = {};
        list = ['serverTime', 'fixTime', 'latitude', 'longitude', 'altitude', 'speed'];
        for (i = 0; i < list.length; i++) {
            result[list[i]] = {
                priority: i,
                name: Strings['position' + list[i].replace(/^\w/g, function (s) {
                    return s.toUpperCase();
                })]
            };
        }
        return result;
    })(),

    updateLatest: function (store, data) {
        var i;
        if (!Ext.isArray(data)) {
            data = [data];
        }
        for (i = 0; i < data.length; i++) {
            if (this.deviceId === data[i].get('deviceId')) {
                this.position = data[i];
                this.updatePosition();
            }
        }
    },

    formatValue: function (value) {
        if (typeof id === 'number') {
            return Number(value.toFixed(2));
        } else {
            return value;
        }
    },

    findAttribute: function (record) {
        return record.get('deviceId') === this.position.get('deviceId') && record.get('attribute') === this.lookupAttribute;
    },

    updatePosition: function () {
        var attributes, store, key, aliasIndex, name, value;
        store = Ext.getStore('Attributes');
        store.removeAll();
        var visibleKeys = ['serverTime','fixTime', 'speed', 'fuel', 'distance', 'odometer', 'battery', 'power', 'alarm'];
        for (key in this.position.data) {
            if (this.position.data.hasOwnProperty(key) && this.keys[key] !== undefined && visibleKeys.includes(key)) {
                store.add(Ext.create('Compassmate.model.Attribute', {
                    priority: this.keys[key].priority,
                    name: this.keys[key].name,
                    value: Compassmate.AttributeFormatter.getFormatter(key)(this.position.get(key))
                }));
            }
        }

        attributes = this.position.get('attributes');
        if (attributes instanceof Object) {
            for (key in attributes) {
                if (attributes.hasOwnProperty(key) && visibleKeys.includes(key)) {
                    this.lookupAttribute = key;
                    aliasIndex = this.aliasesStore.findBy(this.findAttribute, this);
                    if (aliasIndex !== -1) {
                        name = this.aliasesStore.getAt(aliasIndex).get('alias');
                    } else {
                        name = Ext.getStore('PositionAttributes').getAttributeName(key, true);
                    }
                    if (this.position.get('attribute.' + key) !== undefined) {
                        value = Compassmate.AttributeFormatter.getAttributeFormatter(key)(this.position.get('attribute.' + key));
                    } else {
                        value = Compassmate.AttributeFormatter.defaultFormatter(attributes[key]);
                    }
                    store.add(Ext.create('Compassmate.model.Attribute', {
                        priority: 1024,
                        name: name,
                        attribute: key,
                        value: value
                    }));
                }
            }
        }
    },

    selectDevice: function (device) {
        var position;
        this.deviceId = device.get('id');
        position = Ext.getStore('LatestPositions').findRecord('deviceId', this.deviceId, 0, false, false, true);
        if (position) {
            this.position = position;
            this.updatePosition();
        } else {
            this.position = null;
            Ext.getStore('Attributes').removeAll();
        }
    },

    selectReport: function (position) {
        if (position instanceof Compassmate.model.Position) {
            this.deviceId = null;
            this.position = position;
            this.updatePosition();
        }
    },

    deselectFeature: function () {
        this.deviceId = null;
        this.position = null;
        Ext.getStore('Attributes').removeAll();
    },

    clearReport: function () {
        if (!this.deviceId) {
            this.position = null;
            Ext.getStore('Attributes').removeAll();
        }
    },

    onSelectionChange: function (selection, selected) {
        var enabled = selected.length > 0 && selected[0].get('priority') === 1024;
        this.lookupReference('aliasEditButton').setDisabled(!enabled);
    },

    onAliasEditClick: function () {
        var attribute, aliasIndex, attributeAlias, dialog;
        attribute = this.getView().getSelectionModel().getSelection()[0];
        this.lookupAttribute = attribute.get('attribute');
        aliasIndex = this.aliasesStore.findBy(this.findAttribute, this);
        if (aliasIndex !== -1) {
            attributeAlias = this.aliasesStore.getAt(aliasIndex);
        } else {
            attributeAlias = Ext.create('Compassmate.model.AttributeAlias', {
                deviceId: this.position.get('deviceId'),
                attribute: attribute.get('attribute')
            });
            attributeAlias.store = this.aliasesStore;
        }
        dialog = Ext.create('Compassmate.view.dialog.AttributeAlias');
        dialog.down('form').loadRecord(attributeAlias);
        dialog.show();
    },

    updateAliases: function () {
        if (this.position) {
            this.updatePosition();
        }
    }
});
