 

Ext.define('Compassmate.view.Report', {
    extend: 'Ext.panel.Panel',
    xtype: 'reportView',

    requires: [
        'Compassmate.view.ReportController'
    ],

    controller: 'report',

    title: Strings.reportTitle,

    tools: [{
        type: 'close',
        tooltip: Strings.sharedHide,
        handler: 'hideReports'
    }],

    tbar: {
        scrollable: true,
        items: [{
            xtype: 'tbtext',
            html: Strings.sharedType
        }, {
            xtype: 'combobox',
            reference: 'reportTypeField',
            store: 'ReportTypes',
            displayField: 'name',
            valueField: 'key',
            editable: false,
            listeners: {
                change: 'onTypeChange'
            }
        }, '-', {
            text: Strings.reportConfigure,
            handler: 'onConfigureClick'
        }, '-', {
            text: Strings.reportShow,
            reference: 'showButton',
            disabled: true,
            handler: 'onReportClick'
        }, {
            text: Strings.reportExport,
            reference: 'exportButton',
            disabled: true,
            handler: 'onReportClick'
        }, {
            text: Strings.reportClear,
            handler: 'onClearClick'
        }]
    },

    layout: 'card',

    items: [{
        xtype: 'grid',
        itemId: 'grid',
        listeners: {
            selectionchange: 'onSelectionChange'
        },
        columns: {
            defaults: {
                flex: 1,
                minWidth: Compassmate.Style.columnWidthNormal
            },
            items: [
            ]
        },
        style: Compassmate.Style.reportGridStyle
    }, {
        xtype: 'cartesian',
        itemId: 'chart',
        plugins: {
            ptype: 'chartitemevents',
            moveEvents: true
        },
        store: 'ReportRoute',
        interactions: [
            {
                type: 'panzoom',
                enabled: true,
                zoomOnPanGesture: true,
                axes: {
                    left: {
                        allowPan: false,
                        allowZoom: false
                    },
                    bottom: {
                        allowPan: true,
                        allowZoom: true
                    }
                }
            },
            {
                type: 'crosshair'
            }
       ],
        axes: [{
            title: Strings.reportChart,
            type: 'numeric',
            position: 'left'
        }, {
            type: 'time',
            position: 'bottom',
            fields: ['fixTime']
        }],
        listeners: {
            itemclick: 'onChartMarkerClick',
            toggle: function (segmentedButton, button, pressed) {
                var chart = this.up('panel').down('cartesian'),
                    interactions = chart.getInteractions(),
                    value = segmentedButton.getValue();
                interactions[0].setEnabled(value === 1);
                interactions[1].setEnabled(value === 0);
            }
        },
        insetPadding: Compassmate.Style.chartPadding
    }],

    initComponent: function () {
        this.callParent();
        var chart = this.down('cartesian'),
            panzoom = chart.getInteractions()[0];
        this.down('toolbar').add(panzoom.getModeToggleButton());
        panzoom.getModeToggleButton().hide()
    }
});
