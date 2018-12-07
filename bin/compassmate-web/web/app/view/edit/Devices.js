
Ext.define('Compassmate.view.edit.Devices', {
    extend: 'Ext.grid.Panel',
    xtype: 'devicesView',
    config: {
        /**
         * Title/name of the exported xlsx.
         */
        xlsTitle: 'export',
        /**
         * Color of the headers.
         */
        xlsHeaderColor: 'A3C9F1',
        /**
         * Color of the grouping headers.
         */
        xlsGroupHeaderColor: 'EBEBEB',
        /**
         * Color of the summary row.
         */
        xlsSummaryColor: 'FFFFFF',
        /**
         * Show/hide first row with name of exported file.
         */
        xlsShowHeader: false
    },

    requires: [
        'Ext.grid.filters.Filters',
        'Compassmate.AttributeFormatter',
        'Compassmate.view.edit.DevicesController',
        'Compassmate.view.ArrayListFilter'
    ],

    controller: 'devices',

    plugins: 'gridfilters',

    store: 'VisibleDevices',

    stateful: true,
    stateId: 'devices-grid',

    tbar: {
        componentCls: 'toolbar-header-style',
        items: [{
            xtype: 'tbtext',
            html: Strings.deviceTitle,
            baseCls: 'x-panel-header-title-default'
        }, {
            xtype: 'tbfill'
        }, {
            xtype: 'button',
            disabled: true,
            handler: 'onAddClick',
            reference: 'toolbarAddButton',
            glyph: 'xf067@FontAwesome',
            tooltip: Strings.sharedAdd,
            tooltipType: 'title'
        }, {
            xtype: 'button',
            disabled: true,
            handler: 'onEditClick',
            reference: 'toolbarEditButton',
            glyph: 'xf040@FontAwesome',
            tooltip: Strings.sharedEdit,
            tooltipType: 'title'
        }, {
            xtype: 'button',
            disabled: true,
            handler: 'onRemoveClick',
            reference: 'toolbarRemoveButton',
            glyph: 'xf00d@FontAwesome',
            tooltip: Strings.sharedRemove,
            tooltipType: 'title'
        }, {
            xtype: 'button',
            disabled: true,
            handler: 'onGeofencesClick',
            reference: 'toolbarGeofencesButton',
            glyph: 'xf21d@FontAwesome',
            tooltip: Strings.sharedGeofences,
            tooltipType: 'title'
        }, {
            xtype: 'button',
            disabled: true,
            handler: 'onAttributesClick',
            reference: 'toolbarAttributesButton',
            glyph: 'xf0ae@FontAwesome',
            tooltip: Strings.sharedComputedAttributes,
            tooltipType: 'title'
          }, {
              xtype: 'button',
              disabled: true,
              handler: 'onDriversClick',
              reference: 'toolbarDriversButton',
              glyph: 'xf2c2@FontAwesome',
              tooltip: Strings.sharedDrivers,
              tooltipType: 'title'
//        }, {
//            disabled: true,
//            handler: 'onCommandClick',
//            reference: 'deviceCommandButton',
//            glyph: 'xf093@FontAwesome',
//            tooltip: Strings.deviceCommand,
//            tooltipType: 'title'
        }, {
            disabled: false,
            listeners: {
                click: {
                	fn: 'export',
                	name: 'Devices'
                }
            },
            reference: 'exportCommandButton',
            glyph: 'xf019@FontAwesome',
            tooltip: Strings.exportCommand,
            tooltipType: 'title'
        }]
    },

    listeners: {
        selectionchange: 'onSelectionChange'
    },

    viewConfig: {
        getRowClass: function (record) {
            var status = record.get('status');
            if (status) {
                return Ext.getStore('DeviceStatuses').getById(status).get('color');
            }
            return null;
        }
    },

    columns: {
        defaults: {
           flex: 1,
           minWidth: Compassmate.Style.columnWidthNormal
        },
        items: [{
		xtype: 'rownumberer',
		text: Strings.sharedNo,
		Width: 60
	}, {
            text: Strings.sharedName,
            dataIndex: 'name',
            filter: 'string'
        }, {
            text: Strings.deviceIdentifier,
            dataIndex: 'uniqueId',
            filter: 'string',
            hidden: true
        }, {
            text: Strings.sharedPhone,
            dataIndex: 'phone',
            filter: 'string',
            hidden: true
        }, {
            text: Strings.deviceModel,
            dataIndex: 'model',
            filter: 'string',
            hidden: true
        }, {
            text: Strings.deviceContact,
            dataIndex: 'contact',
            filter: 'string',
            hidden: true
        }, {
            text: Strings.groupDialog,
            dataIndex: 'groupId',
            hidden: true,
            filter: {
                type: 'list',
                labelField: 'name',
                store: 'Groups'
            },
            renderer: Compassmate.AttributeFormatter.getFormatter('groupId')
        }, {
            text: Strings.sharedGeofences,
            dataIndex: 'geofenceIds',
            hidden: true,
            filter: {
                type: 'arraylist',
                idField: 'id',
                labelField: 'name',
                store: 'Geofences'
            },
            renderer: function (value) {
                var i, name, result = '';
                if (Ext.isArray(value)) {
                    for (i = 0; i < value.length; i++) {
                        name = Compassmate.AttributeFormatter.geofenceIdFormatter(value[i]);
                        if (name) {
                            result += name + (i < value.length - 1 ? ', ' : '');
                        }
                    }
                }
                return result;
            }
        }, {
            text: Strings.deviceStatus,
            dataIndex: 'status',
            filter: {
                type: 'list',
                labelField: 'name',
                store: 'DeviceStatuses'
            },
            renderer: function (value) {
                var status;
                if (value) {
                    status = Ext.getStore('DeviceStatuses').getById(value);
                    if (status) {
                        return status.get('name');
                    }
                }
                return null;
            }
        }, {
            text: Strings.deviceLastUpdate,
            dataIndex: 'lastUpdate',
            renderer: Compassmate.AttributeFormatter.getFormatter('lastUpdate')
        }]
    }
});
