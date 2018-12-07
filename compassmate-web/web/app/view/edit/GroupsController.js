 

Ext.define('Compassmate.view.edit.GroupsController', {
    extend: 'Compassmate.view.edit.ToolbarController',
    alias: 'controller.groups',

    requires: [
        'Compassmate.view.dialog.Group',
        'Compassmate.view.permissions.GroupGeofences',
        'Compassmate.view.permissions.GroupAttributes',
        'Compassmate.view.permissions.GroupDrivers',
        'Compassmate.view.BaseWindow',
        'Compassmate.model.Group'
    ],

    objectModel: 'Compassmate.model.Group',
    objectDialog: 'Compassmate.view.dialog.Group',
    removeTitle: Strings.groupDialog,

    init: function () {
        this.lookupReference('toolbarDriversButton').setHidden(
            Compassmate.app.getVehicleFeaturesDisabled() || Compassmate.app.getBooleanAttributePreference('ui.disableDrivers'));
        this.lookupReference('toolbarAttributesButton').setHidden(
            Compassmate.app.getBooleanAttributePreference('ui.disableComputedAttributes'));
    },

    onGeofencesClick: function () {
        var admin, group;
        admin = Compassmate.app.getUser().get('admin');
        group = this.getView().getSelectionModel().getSelection()[0];
        Ext.create('Compassmate.view.BaseWindow', {
            title: Strings.sharedGeofences,
            items: {
                xtype: 'groupGeofencesView',
                baseObjectName: 'groupId',
                linkObjectName: 'geofenceId',
                storeName: admin ? 'AllGeofences' : 'Geofences',
                baseObject: group.getId()
            }
        }).show();
    },

    onAttributesClick: function () {
        var admin, group;
        admin = Compassmate.app.getUser().get('admin');
        group = this.getView().getSelectionModel().getSelection()[0];
        Ext.create('Compassmate.view.BaseWindow', {
            title: Strings.sharedComputedAttributes,
            items: {
                xtype: 'groupAttributesView',
                baseObjectName: 'groupId',
                linkObjectName: 'attributeId',
                storeName: admin ? 'AllComputedAttributes' : 'ComputedAttributes',
                baseObject: group.getId()
            }
        }).show();
    },

    onDriversClick: function () {
        var admin, group;
        admin = Compassmate.app.getUser().get('admin');
        group = this.getView().getSelectionModel().getSelection()[0];
        Ext.create('Compassmate.view.BaseWindow', {
            title: Strings.sharedDrivers,
            items: {
                xtype: 'groupDriversView',
                baseObjectName: 'groupId',
                linkObjectName: 'driverId',
                storeName: admin ? 'AllDrivers' : 'Drivers',
                baseObject: group.getId()
            }
        }).show();
    },

    onSelectionChange: function (selection, selected) {
        var disabled = selected.length === 0;
        this.lookupReference('toolbarGeofencesButton').setDisabled(disabled);
        this.lookupReference('toolbarAttributesButton').setDisabled(disabled);
        this.lookupReference('toolbarDriversButton').setDisabled(disabled);
        this.callParent(arguments);
    }
});
