 

Ext.define('Compassmate.view.permissions.UserGroups', {
    extend: 'Compassmate.view.permissions.Base',
    xtype: 'userGroupsView',

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
