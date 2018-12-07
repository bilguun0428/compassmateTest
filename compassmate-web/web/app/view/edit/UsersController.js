
Ext.define('Compassmate.view.edit.UsersController', {
    extend: 'Compassmate.view.edit.ToolbarController',
    alias: 'controller.users',

    requires: [
        'Compassmate.view.dialog.User',
        'Compassmate.view.permissions.UserDevices',
        'Compassmate.view.permissions.UserGroups',
        'Compassmate.view.permissions.UserGeofences',
        'Compassmate.view.permissions.UserCalendars',
        'Compassmate.view.permissions.UserUsers',
        'Compassmate.view.permissions.UserAttributes',
        'Compassmate.view.permissions.UserDrivers',
        'Compassmate.view.Notifications',
        'Compassmate.view.BaseWindow',
        'Compassmate.model.User'
    ],

    objectModel: 'Compassmate.model.User',
    objectDialog: 'Compassmate.view.dialog.User',
    removeTitle: Strings.settingsUser,

    init: function () {
        Ext.getStore('Users').load();
        this.lookupReference('userUsersButton').setHidden(!Compassmate.app.getUser().get('admin'));
        this.lookupReference('userDriversButton').setHidden(
            Compassmate.app.getVehicleFeaturesDisabled() || Compassmate.app.getBooleanAttributePreference('ui.disableDrivers'));
        this.lookupReference('userAttributesButton').setHidden(
            Compassmate.app.getBooleanAttributePreference('ui.disableComputedAttributes'));
        this.lookupReference('userCalendarsButton').setHidden(
            Compassmate.app.getBooleanAttributePreference('ui.disableCalendars'));
    },

    onEditClick: function () {
        var dialog, user = this.getView().getSelectionModel().getSelection()[0];
        dialog = Ext.create('Compassmate.view.dialog.User', {
            selfEdit: user.get('id') === Compassmate.app.getUser().get('id')
        });
        dialog.down('form').loadRecord(user);
        dialog.show();
    },

    onAddClick: function () {
        var user, dialog;
        user = Ext.create('Compassmate.model.User');
        if (Compassmate.app.getUser().get('admin')) {
            user.set('deviceLimit', -1);
        }
        if (Compassmate.app.getUser().get('expirationTime')) {
            user.set('expirationTime', Compassmate.app.getUser().get('expirationTime'));
        }
        dialog = Ext.create('Compassmate.view.dialog.User');
        dialog.down('form').loadRecord(user);
        dialog.show();
    },

    onDevicesClick: function () {
        var user = this.getView().getSelectionModel().getSelection()[0];
        Ext.getStore('AllGroups').load();
        Ext.create('Compassmate.view.BaseWindow', {
            title: Strings.deviceTitle,
            items: {
                xtype: 'userDevicesView',
                baseObjectName: 'userId',
                linkObjectName: 'deviceId',
                storeName: 'AllDevices',
                linkStoreName: 'Devices',
                baseObject: user.getId()
            }
        }).show();
    },

    onGroupsClick: function () {
        var user = this.getView().getSelectionModel().getSelection()[0];
        Ext.create('Compassmate.view.BaseWindow', {
            title: Strings.settingsGroups,
            items: {
                xtype: 'userGroupsView',
                baseObjectName: 'userId',
                linkObjectName: 'groupId',
                storeName: 'AllGroups',
                linkStoreName: 'Groups',
                baseObject: user.getId()
            }
        }).show();
    },

    onGeofencesClick: function () {
        var user = this.getView().getSelectionModel().getSelection()[0];
        Ext.create('Compassmate.view.BaseWindow', {
            title: Strings.sharedGeofences,
            items: {
                xtype: 'userGeofencesView',
                baseObjectName: 'userId',
                linkObjectName: 'geofenceId',
                storeName: 'AllGeofences',
                linkStoreName: 'Geofences',
                baseObject: user.getId()
            }
        }).show();
    },

    onNotificationsClick: function () {
        var user = this.getView().getSelectionModel().getSelection()[0];
        Ext.create('Compassmate.view.BaseWindow', {
            title: Strings.sharedNotifications,
            items: {
                xtype: 'notificationsView',
                user: user
            }
        }).show();
    },

    onCalendarsClick: function () {
        var user = this.getView().getSelectionModel().getSelection()[0];
        Ext.create('Compassmate.view.BaseWindow', {
            title: Strings.sharedCalendars,
            items: {
                xtype: 'userCalendarsView',
                baseObjectName: 'userId',
                linkObjectName: 'calendarId',
                storeName: 'AllCalendars',
                linkStoreName: 'Calendars',
                baseObject: user.getId()
            }
        }).show();
    },

    onUsersClick: function () {
        var user = this.getView().getSelectionModel().getSelection()[0];
        Ext.create('Compassmate.view.BaseWindow', {
            title: Strings.settingsUsers,
            items: {
                xtype: 'userUsersView',
                baseObjectName: 'userId',
                linkObjectName: 'managedUserId',
                storeName: 'Users',
                baseObject: user.getId()
            }
        }).show();
    },

    onAttributesClick: function () {
        var user = this.getView().getSelectionModel().getSelection()[0];
        Ext.create('Compassmate.view.BaseWindow', {
            title: Strings.sharedComputedAttributes,
            items: {
                xtype: 'userAttributesView',
                baseObjectName: 'userId',
                linkObjectName: 'attributeId',
                storeName: 'AllComputedAttributes',
                linkStoreName: 'ComputedAttributes',
                baseObject: user.getId()
            }
        }).show();
    },

    onDriversClick: function () {
        var user = this.getView().getSelectionModel().getSelection()[0];
        Ext.create('Compassmate.view.BaseWindow', {
            title: Strings.sharedDrivers,
            items: {
                xtype: 'userDriversView',
                baseObjectName: 'userId',
                linkObjectName: 'driverId',
                storeName: 'AllDrivers',
                linkStoreName: 'Drivers',
                baseObject: user.getId()
            }
        }).show();
    },


    onSelectionChange: function (selection, selected) {
        var disabled = selected.length === 0;
        this.lookupReference('userDevicesButton').setDisabled(disabled);
        this.lookupReference('userGroupsButton').setDisabled(disabled);
        this.lookupReference('userGeofencesButton').setDisabled(disabled);
        this.lookupReference('userNotificationsButton').setDisabled(disabled);
        this.lookupReference('userCalendarsButton').setDisabled(disabled);
        this.lookupReference('userAttributesButton').setDisabled(disabled);
        this.lookupReference('userDriversButton').setDisabled(disabled);
        this.lookupReference('userUsersButton').setDisabled(disabled || selected[0].get('userLimit') === 0);
        this.callParent(arguments);
    }
});
