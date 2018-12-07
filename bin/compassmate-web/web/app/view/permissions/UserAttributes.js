 

Ext.define('Compassmate.view.permissions.UserAttributes', {
    extend: 'Compassmate.view.permissions.Base',
    xtype: 'userAttributesView',

    requires: [
        'Ext.grid.filters.Filters'
    ],

    plugins: 'gridfilters',

    columns: {
        items: [{
            text: Strings.sharedDescription,
            dataIndex: 'description',
            flex: 1,
            minWidth: Compassmate.Style.columnWidthNormal,
            filter: 'string'
        }, {
            text: Strings.sharedAttribute,
            dataIndex: 'attribute',
            flex: 1,
            minWidth: Compassmate.Style.columnWidthNormal,
            filter: {
                type: 'list',
                labelField: 'name',
                store: 'PositionAttributes'
            },
            renderer: function (value) {
                return Ext.getStore('PositionAttributes').getAttributeName(value);
            }
        }]
    }
});
