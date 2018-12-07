 

Ext.define('Compassmate.view.permissions.UserDevices', {
    extend: 'Compassmate.view.permissions.Base',
    xtype: 'userDevicesView',

    requires: [
        'Ext.grid.filters.Filters',
        'Compassmate.AttributeFormatter'
    ],

    plugins: 'gridfilters',

    columns: {
        items: [{
            text: Strings.sharedName,
            dataIndex: 'name',
            flex: 1,
            minWidth: Compassmate.Style.columnWidthNormal,
            filter: 'string'
        }, {
            text: Strings.deviceIdentifier,
            dataIndex: 'uniqueId',
            flex: 1,
            minWidth: Compassmate.Style.columnWidthNormal,
            filter: 'string'
        }, {
            text: Strings.sharedPhone,
            dataIndex: 'phone',
            flex: 1,
            minWidth: Compassmate.Style.columnWidthNormal,
            hidden: true,
            filter: 'string'
        }, {
            text: Strings.deviceModel,
            dataIndex: 'model',
            flex: 1,
            minWidth: Compassmate.Style.columnWidthNormal,
            hidden: true,
            filter: 'string'
        }, {
            text: Strings.deviceContact,
            dataIndex: 'contact',
            flex: 1,
            minWidth: Compassmate.Style.columnWidthNormal,
            hidden: true,
            filter: 'string'
        }, {
            text: Strings.groupDialog,
            dataIndex: 'groupId',
            flex: 1,
            minWidth: Compassmate.Style.columnWidthNormal,
            hidden: true,
            filter: {
                type: 'list',
                labelField: 'name',
                store: 'AllGroups'
            },
            renderer: Compassmate.AttributeFormatter.getFormatter('groupId')
        }]
    }
});
