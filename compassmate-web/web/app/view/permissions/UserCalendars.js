
Ext.define('Compassmate.view.permissions.UserCalendars', {
    extend: 'Compassmate.view.permissions.Base',
    xtype: 'userCalendarsView',

    requires: [
        'Ext.grid.filters.Filters'
    ],

    plugins: 'gridfilters',

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
