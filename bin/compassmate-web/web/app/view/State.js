 

Ext.define('Compassmate.view.State', {
    extend: 'Ext.grid.Panel',
    xtype: 'stateView',

    requires: [
        'Compassmate.view.StateController'
    ],

    controller: 'state',
    store: 'Attributes',

    stateful: true,
    stateId: 'state-grid',

    tbar: {
        componentCls: 'toolbar-header-style',
        items: [{
            xtype: 'tbtext',
            html: Strings.stateTitle,
            baseCls: 'x-panel-header-title-default'
        }, {
            xtype: 'tbfill'
        }, {
            xtype: 'button',
            disabled: true,
            handler: 'onAliasEditClick',
            reference: 'aliasEditButton',
            glyph: 'xf02b@FontAwesome',
            tooltip: Strings.sharedEdit,
            tooltipType: 'title'
        }]
    },

    listeners: {
        selectionchange: 'onSelectionChange'
    },

    columns: {
        defaults: {
            minWidth: Compassmate.Style.columnWidthNormal,
            flex: 1
        },
        items: [{
            text: Strings.stateName,
            dataIndex: 'name'
        }, {
            text: Strings.stateValue,
            dataIndex: 'value',
            renderer: function (value, metaData, record) {
                if (record.get('attribute') === 'alarm') {
                    metaData.tdCls = 'view-color-red';
                }
                return value;
            }
        }]
    }
});
