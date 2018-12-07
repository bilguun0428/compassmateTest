 

Ext.define('Compassmate.view.Notifications', {
    extend: 'Ext.grid.Panel',
    xtype: 'notificationsView',

    requires: [
        'Compassmate.view.NotificationsController'
    ],

    controller: 'notificationsController',
    store: 'Notifications',

    selModel: {
        selType: 'cellmodel'
    },

    viewConfig: {
        markDirty: false
    },

    columns: {
        defaults: {
            flex: 1,
            minWidth: Compassmate.Style.columnWidthNormal
        },
        items: [{
            text: Strings.notificationType,
            dataIndex: 'type',
            flex: 2,
            renderer: function (value) {
                return Compassmate.app.getEventString(value);
            }
        }, {
            text: Strings.notificationWeb,
            dataIndex: 'web',
            xtype: 'checkcolumn',
            listeners: {
                checkChange: 'onCheckChange'
            }
        }, {
            text: Strings.notificationMail,
            dataIndex: 'mail',
            xtype: 'checkcolumn',
            listeners: {
                checkChange: 'onCheckChange'
            }
        }, {
            text: Strings.notificationSms,
            dataIndex: 'sms',
            xtype: 'checkcolumn',
            listeners: {
                checkChange: 'onCheckChange'
            }
        }]
    }
});
