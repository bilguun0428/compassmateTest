 

Ext.define('Compassmate.view.SettingsMenuController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.settings',

    requires: [
        'Compassmate.view.dialog.LoginController',
        'Compassmate.view.dialog.User',
        'Compassmate.view.dialog.Server',
        'Compassmate.view.edit.Users',
        'Compassmate.view.edit.Groups',
        'Compassmate.view.edit.Geofences',
        'Compassmate.view.edit.Drivers',
        'Compassmate.view.Notifications',
        'Compassmate.view.edit.AttributeAliases',
        'Compassmate.view.edit.ComputedAttributes',
        'Compassmate.view.Statistics',
        'Compassmate.view.dialog.DeviceDistance',
        'Compassmate.view.edit.Calendars',
        'Compassmate.view.BaseWindow'
    ],

    init: function () {
        var admin, manager, readonly, deviceReadonly;
        admin = Compassmate.app.getUser().get('admin');
        manager = Compassmate.app.getUser().get('userLimit') !== 0;
        readonly = Compassmate.app.getPreference('readonly', false);
        deviceReadonly = Compassmate.app.getUser().get('deviceReadonly');
        if (admin) {
            this.lookupReference('settingsServerButton').setHidden(false);
            this.lookupReference('settingsStatisticsButton').setHidden(false);
            this.lookupReference('settingsDeviceDistanceButton').setHidden(Compassmate.app.getVehicleFeaturesDisabled());
        }
        if (admin || manager) {
            this.lookupReference('settingsUsersButton').setHidden(false);
        }
        if (admin || !readonly) {
            this.lookupReference('settingsUserButton').setHidden(false);
            this.lookupReference('settingsGroupsButton').setHidden(false);
            this.lookupReference('settingsGeofencesButton').setHidden(false);
            this.lookupReference('settingsNotificationsButton').setHidden(false);
//            this.lookupReference('settingsCalendarsButton').setHidden(
//                Compassmate.app.getBooleanAttributePreference('ui.disableCalendars'));
            this.lookupReference('settingsDriversButton').setHidden(
                Compassmate.app.getVehicleFeaturesDisabled() || Compassmate.app.getBooleanAttributePreference('ui.disableDrivers'));
        }
        if (admin || !deviceReadonly && !readonly) {
//            this.lookupReference('settingsAttributeAliasesButton').setHidden(false);
            this.lookupReference('settingsComputedAttributesButton').setHidden(
                Compassmate.app.getBooleanAttributePreference('ui.disableComputedAttributes'));
        }
    },

    onUserClick: function () {
        var dialog = Ext.create('Compassmate.view.dialog.User', {
            selfEdit: true
        });
        dialog.down('form').loadRecord(Compassmate.app.getUser());
        dialog.lookupReference('testNotificationButton').setHidden(false);
        dialog.show();
    },

    onGroupsClick: function () {
        Ext.create('Compassmate.view.BaseWindow', {
            title: Strings.settingsGroups,
            items: {
                xtype: 'groupsView'
            }
        }).show();
    },

    onGeofencesClick: function () {
        Ext.create('Compassmate.view.BaseWindow', {
            title: Strings.sharedGeofences,
            items: {
                xtype: 'geofencesView'
            }
        }).show();
    },

    onServerClick: function () {
        var dialog = Ext.create('Compassmate.view.dialog.Server');
        dialog.down('form').loadRecord(Compassmate.app.getServer());
        dialog.show();
    },

    onUsersClick: function () {
        Ext.create('Compassmate.view.BaseWindow', {
            title: Strings.settingsUsers,
            items: {
                xtype: 'usersView'
            }
        }).show();
    },

    onNotificationsClick: function () {
        var user = Compassmate.app.getUser();
        Ext.create('Compassmate.view.BaseWindow', {
            title: Strings.sharedNotifications,
            items: {
                xtype: 'notificationsView',
                user: user
            }
        }).show();
    },

    onAttributeAliasesClick: function () {
        Ext.create('Compassmate.view.BaseWindow', {
            title: Strings.sharedAttributeAliases,
            items: {
                xtype: 'attributeAliasesView'
            }
        }).show();
    },

    onComputedAttributesClick: function () {
        Ext.create('Compassmate.view.BaseWindow', {
            title: Strings.sharedComputedAttributes,
            items: {
                xtype: 'computedAttributesView'
            }
        }).show();
    },

    onStatisticsClick: function () {
        Ext.create('Compassmate.view.BaseWindow', {
            title: Strings.statisticsTitle,
            items: {
                xtype: 'statisticsView'
            }
        }).show();
    },

    onDeviceDistanceClick: function () {
        var dialog = Ext.create('Compassmate.view.dialog.DeviceDistance');
        dialog.show();
    },

    onCalendarsClick: function () {
        Ext.create('Compassmate.view.BaseWindow', {
            title: Strings.sharedCalendars,
            items: {
                xtype: 'calendarsView'
            }
        }).show();
    },

    onDriversClick: function () {
        Ext.create('Compassmate.view.BaseWindow', {
            title: Strings.sharedDrivers,
            items: {
                xtype: 'driversView'
            }
        }).show();
    },

    onLogoutClick: function () {
        Ext.create('Compassmate.view.dialog.LoginController').logout();
    }
});
