
Ext.define('Compassmate.view.edit.Calendars', {
    extend: 'Ext.grid.Panel',
    xtype: 'calendarsView',

    requires: [
        'Ext.grid.filters.Filters',
        'Compassmate.view.edit.CalendarsController',
        'Compassmate.view.edit.Toolbar'
    ],

    plugins: 'gridfilters',

    controller: 'calendars',
    store: 'Calendars',

    tbar: {
        xtype: 'editToolbar'
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
        }]
    }
});
