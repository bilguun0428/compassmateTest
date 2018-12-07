 

Ext.define('Compassmate.view.edit.ComputedAttributes', {
    extend: 'Ext.grid.Panel',
    xtype: 'computedAttributesView',

    requires: [
        'Ext.grid.filters.Filters',
        'Compassmate.view.edit.ComputedAttributesController',
        'Compassmate.view.edit.Toolbar'
    ],

    plugins: 'gridfilters',

    controller: 'computedAttributes',
    store: 'ComputedAttributes',

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
            text: Strings.sharedDescription,
            dataIndex: 'description',
            filter: 'string'
        }, {
            text: Strings.sharedAttribute,
            dataIndex: 'attribute',
            filter: {
                type: 'list',
                labelField: 'name',
                store: 'PositionAttributes'
            },
            renderer: function (value) {
                return Ext.getStore('PositionAttributes').getAttributeName(value);
            }
        }, {
            text: Strings.sharedExpression,
            dataIndex: 'expression'
        }, {
            text: Strings.sharedType,
            dataIndex: 'type',
            filter: {
                type: 'list',
                labelField: 'name',
                store: 'AttributeValueTypes'
            },
            renderer: function (value) {
                var type = Ext.getStore('AttributeValueTypes').getById(value);
                if (type) {
                    return type.get('name');
                } else {
                    return value;
                }
            }
        }]
    }
});
