
Ext.define('Compassmate.view.edit.AttributeAliases', {
    extend: 'Ext.grid.Panel',
    xtype: 'attributeAliasesView',

    requires: [
        'Compassmate.view.edit.AttributeAliasesController',
        'Compassmate.view.edit.Toolbar'
    ],

    controller: 'attributeAliases',

    tbar: {
        xtype: 'editToolbar',
        items: ['-', {
            xtype: 'tbtext',
            html: Strings.sharedDevice
        }, {
            xtype: 'combobox',
            reference: 'deviceField',
            store: 'Devices',
            displayField: 'name',
            valueField: 'id',
            editable: false,
            listeners: {
                change: 'onDeviceChange'
            }
        }]
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
            text: Strings.sharedAttribute,
            dataIndex: 'attribute'
        }, {
            text: Strings.sharedAlias,
            dataIndex: 'alias'
        }]
    }
});
