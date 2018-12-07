 

Ext.define('Compassmate.view.edit.Geofences', {
    extend: 'Ext.grid.Panel',
    xtype: 'geofencesView',

    requires: [
        'Ext.grid.filters.Filters',
        'Compassmate.view.edit.GeofencesController',
        'Compassmate.view.edit.Toolbar'
    ],

    plugins: 'gridfilters',

    controller: 'geofences',
    store: 'Geofences',

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
            dataIndex: 'description',
            filter: 'string'
        }]
    }
});
