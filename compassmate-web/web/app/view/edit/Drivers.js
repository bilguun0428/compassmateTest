 

Ext.define('Compassmate.view.edit.Drivers', {
    extend: 'Ext.grid.Panel',
    xtype: 'driversView',

    requires: [
        'Ext.grid.filters.Filters',
        'Compassmate.view.edit.DriversController',
        'Compassmate.view.edit.Toolbar'
    ],

    plugins: 'gridfilters',

    controller: 'drivers',
    store: 'Drivers',

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
        }, {
            text: Strings.sharedDescription,
            dataIndex: 'uniqueId',
            filter: 'string'
        }]
    }
});
