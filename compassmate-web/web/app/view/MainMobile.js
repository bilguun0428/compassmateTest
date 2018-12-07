 

Ext.define('Compassmate.view.MainMobile', {
    extend: 'Ext.container.Viewport',
    alias: 'widget.mainMobile',

    id: 'rootPanel',

    requires: [
        'Compassmate.view.edit.Devices',
        'Compassmate.view.State',
        'Compassmate.view.Report',
        'Compassmate.view.Events',
        'Compassmate.view.map.Map'
    ],

    layout: 'card',

    items: [{
        layout: 'border',

        defaults: {
            header: false,
            collapsible: true,
            split: true
        },

        items: [{
            region: 'east',
            xtype: 'stateView',
            title: Strings.stateTitle,
            flex: 4,
            collapsed: true,
            collapseMode: 'mini',
            titleCollapse: true,
            floatable: false,
            stateId: 'mobile-state-grid'
        }, {
            region: 'center',
            xtype: 'mapView',
            collapsible: false,
            flex: 2
        }, {
            region: 'south',
            xtype: 'devicesView',
            title: Strings.deviceTitle,
            flex: 1,
            collapsed: true,
            titleCollapse: true,
            floatable: false,
            stateId: 'mobile-devices-grid'
        }]
    }, {
        xtype: 'reportView'
    }, {
        xtype: 'eventsView'
    }]
});
