 

Ext.define('Compassmate.view.permissions.GroupDrivers', {
    extend: 'Compassmate.view.permissions.Base',
    xtype: 'groupDriversView',

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
        }, {
            text: Strings.deviceIdentifier,
            dataIndex: 'uniqueId',
            flex: 1,
            minWidth: Compassmate.Style.columnWidthNormal,
            filter: 'string'
        }]
    }
});
