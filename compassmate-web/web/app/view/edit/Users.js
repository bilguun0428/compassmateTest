
Ext.define('Compassmate.view.edit.Users', {
    extend: 'Ext.grid.Panel',
    xtype: 'usersView',

    requires: [
        'Ext.grid.filters.Filters',
        'Compassmate.view.edit.UsersController',
        'Compassmate.view.edit.Toolbar'
    ],

    controller: 'users',
    store: 'Users',

    plugins: 'gridfilters',

    tbar: {
        xtype: 'editToolbar',
        items: [{
            disabled: true,
            handler: 'onGeofencesClick',
            reference: 'userGeofencesButton',
            glyph: 'xf21d@FontAwesome',
            tooltip: Strings.sharedGeofences,
            tooltipType: 'title'
        }, {
            disabled: true,
            handler: 'onDevicesClick',
            reference: 'userDevicesButton',
            glyph: 'xf248@FontAwesome',
            tooltip: Strings.deviceTitle,
            tooltipType: 'title'
        }, {
            disabled: true,
            handler: 'onGroupsClick',
            reference: 'userGroupsButton',
            glyph: 'xf247@FontAwesome',
            tooltip: Strings.settingsGroups,
            tooltipType: 'title'
        }, {
            disabled: true,
            handler: 'onUsersClick',
            reference: 'userUsersButton',
            glyph: 'xf0c0@FontAwesome',
            tooltip: Strings.settingsUsers,
            tooltipType: 'title'
        }, {
            disabled: true,
            handler: 'onNotificationsClick',
            reference: 'userNotificationsButton',
            glyph: 'xf003@FontAwesome',
            tooltip: Strings.sharedNotifications,
            tooltipType: 'title'
        }, {
            disabled: true,
            handler: 'onCalendarsClick',
            reference: 'userCalendarsButton',
            glyph: 'xf073@FontAwesome',
            tooltip: Strings.sharedCalendars,
            tooltipType: 'title'
        }, {
            disabled: true,
            handler: 'onAttributesClick',
            reference: 'userAttributesButton',
            glyph: 'xf0ae@FontAwesome',
            tooltip: Strings.sharedComputedAttributes,
            tooltipType: 'title'
        }, {
            disabled: true,
            handler: 'onDriversClick',
            reference: 'userDriversButton',
            glyph: 'xf2c2@FontAwesome',
            tooltip: Strings.sharedDrivers,
            tooltipType: 'title'
        }]
    },

    listeners: {
        selectionchange: 'onSelectionChange'
    },

    columns: {
        defaults: {
            flex: 1,
            minWidth: Compassmate.Style.columnWidthNormal
        },
        items: [{
            text: Strings.sharedName,
            dataIndex: 'name',
            filter: 'string'
        }, {
            text: Strings.userEmail,
            dataIndex: 'email',
            filter: 'string'
        }, {
            text: Strings.userAdmin,
            dataIndex: 'admin',
            filter: 'boolean'
        }, {
            text: Strings.serverReadonly,
            dataIndex: 'readonly',
            hidden: true,
            filter: 'boolean'
        }, {
            text: Strings.userDeviceReadonly,
            dataIndex: 'deviceReadonly',
            hidden: true,
            filter: 'boolean'
        }, {
            text: Strings.userDisabled,
            dataIndex: 'disabled',
            filter: 'boolean'
        }, {
            text: Strings.userExpirationTime,
            dataIndex: 'expirationTime',
            hidden: true,
            renderer: Compassmate.AttributeFormatter.getFormatter('expirationTime'),
            filter: 'date'
        }]
    }
});
