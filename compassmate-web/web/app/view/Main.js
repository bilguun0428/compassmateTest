 

Ext.define('Compassmate.view.Main', {
    extend: 'Ext.container.Viewport',
    alias: 'widget.main',

    requires: [
        'Compassmate.view.MainController',
        'Compassmate.view.edit.Devices',
        'Compassmate.view.State',
        'Compassmate.view.Report',
        'Compassmate.view.Events',
        'Compassmate.view.map.Map'
    ],

    controller: 'mainController',

    layout: 'border',

    defaults: {
        header: false,
        collapsible: true,
        split: true
    },

    items: [{
        region: 'west',
        layout: 'border',
        width: Compassmate.Style.deviceWidth,
        title: Strings.devicesAndState,
        titleCollapse: true,
        floatable: false,
        stateful: true,
        stateId: 'devices-and-state-panel',

        defaults: {
            split: true,
            flex: 1
        },

        items: [{
            region: 'center',
            xtype: 'devicesView'
        }, {
            region: 'south',
            xtype: 'stateView'
        }]
    }, {
        region: 'south',
        xtype: 'reportView',
        reference: 'reportView',
        height: Compassmate.Style.reportHeight,
        collapsed: true,
        titleCollapse: true,
        floatable: false
    }, {
        region: 'center',
        xtype: 'mapView',
        collapsible: false
    }, {
        region: 'east',
        xtype: 'eventsView',
        width: Compassmate.Style.deviceWidth,
        collapsed: true,
        titleCollapse: true,
        floatable: false
    }]
});
