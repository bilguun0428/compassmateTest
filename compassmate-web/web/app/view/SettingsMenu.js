 

Ext.define('Compassmate.view.SettingsMenu', {
    extend: 'Ext.button.Button',
    xtype: 'settingsMenu',

    requires: [
        'Compassmate.view.SettingsMenuController'
    ],

    glyph: 'xf013@FontAwesome',
    tooltip: Strings.settingsTitle,
    tooltipType: 'title',

    menu: {
        controller: 'settings',

        items: [{
            hidden: true,
            text: Strings.settingsUser,
            glyph: 'xf007@FontAwesome',
            handler: 'onUserClick',
            reference: 'settingsUserButton'
        }, {
            hidden: true,
            text: Strings.settingsGroups,
            glyph: 'xf247@FontAwesome',
            handler: 'onGroupsClick',
            reference: 'settingsGroupsButton'
        }, {
            hidden: true,
            text: Strings.sharedDrivers,
            glyph: 'xf2c2@FontAwesome',
            handler: 'onDriversClick',
            reference: 'settingsDriversButton'
        }, {
            hidden: true,
            text: Strings.sharedGeofences,
            glyph: 'xf21d@FontAwesome',
            handler: 'onGeofencesClick',
            reference: 'settingsGeofencesButton'
        }, {
            hidden: true,
            text: Strings.settingsServer,
            glyph: 'xf0ad@FontAwesome',
            handler: 'onServerClick',
            reference: 'settingsServerButton'
        }, {
            hidden: true,
            text: Strings.settingsUsers,
            glyph: 'xf0c0@FontAwesome',
            handler: 'onUsersClick',
            reference: 'settingsUsersButton'
        }, {
            hidden: true,
            text: Strings.sharedNotifications,
            glyph: 'xf003@FontAwesome',
            handler: 'onNotificationsClick',
            reference: 'settingsNotificationsButton'
//        }, {
//            hidden: true,
//            text: Strings.sharedAttributeAliases,
//            glyph: 'xf02c@FontAwesome',
//            handler: 'onAttributeAliasesClick',
//            reference: 'settingsAttributeAliasesButton'
        }, {
            hidden: true,
            text: Strings.sharedComputedAttributes,
            glyph: 'xf0ae@FontAwesome',
            handler: 'onComputedAttributesClick',
            reference: 'settingsComputedAttributesButton'
        }, {
            hidden: true,
            text: Strings.sharedDeviceDistance,
            glyph: 'xf0e4@FontAwesome',
            handler: 'onDeviceDistanceClick',
            reference: 'settingsDeviceDistanceButton'
        }, {
            hidden: true,
            text: Strings.statisticsTitle,
            glyph: 'xf080@FontAwesome',
            handler: 'onStatisticsClick',
            reference: 'settingsStatisticsButton'
//        }, {
//            hidden: true,
//            text: Strings.sharedCalendars,
//            glyph: 'xf073@FontAwesome',
//            handler: 'onCalendarsClick',
//            reference: 'settingsCalendarsButton'
        }, {
            text: Strings.loginLogout,
            glyph: 'xf08b@FontAwesome',
            handler: 'onLogoutClick'
        }]
    }
});
