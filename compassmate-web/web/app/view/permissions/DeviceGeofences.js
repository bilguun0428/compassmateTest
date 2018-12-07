 

Ext.define('Compassmate.view.permissions.DeviceGeofences', {
    extend: 'Compassmate.view.permissions.Base',
    xtype: 'deviceGeofencesView',

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
