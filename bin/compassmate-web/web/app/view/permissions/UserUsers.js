 

Ext.define('Compassmate.view.permissions.UserUsers', {
    extend: 'Compassmate.view.permissions.Base',
    xtype: 'userUsersView',

    columns: {
        items: [{
            text: Strings.sharedName,
            dataIndex: 'name',
            flex: 1,
            minWidth: Compassmate.Style.columnWidthNormal,
            filter: 'string'
        }]
    }
});
