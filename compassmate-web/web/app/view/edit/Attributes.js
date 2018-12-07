 

Ext.define('Compassmate.view.edit.Attributes', {
    extend: 'Ext.grid.Panel',
    xtype: 'attributesView',

    requires: [
        'Ext.grid.filters.Filters',
        'Compassmate.view.edit.AttributesController',
        'Compassmate.view.edit.Toolbar'
    ],

    plugins: 'gridfilters',

    controller: 'attributes',

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
            filter: 'string',
            renderer: function (value) {
                var attribute;
                if (this.attributesStore) {
                    attribute = Ext.getStore(this.attributesStore).getById(value);
                }
                return attribute && attribute.get('name') ? attribute.get('name') : value;
            }
        }, {
            text: Strings.stateValue,
            dataIndex: 'value',
            renderer: function (value, metaData, record) {
                var attribute;
                if (this.attributesStore) {
                    attribute = Ext.getStore(this.attributesStore).getById(record.get('name'));
                }
                if (attribute && attribute.get('dataType') === 'speed') {
                    return Ext.getStore('SpeedUnits').formatValue(value, Compassmate.app.getPreference('speedUnit', 'kn'), true);
                } else if (attribute && attribute.get('dataType') === 'distance') {
                    return Ext.getStore('DistanceUnits').formatValue(value, Compassmate.app.getPreference('distanceUnit', 'km'), true);
                } else {
                    return value;
                }
            }
        }]
    }
});
