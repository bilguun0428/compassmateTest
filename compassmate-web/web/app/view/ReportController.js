
Ext.define('Compassmate.view.ReportController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.report',

    requires: [
        'Compassmate.AttributeFormatter',
        'Compassmate.model.Position',
        'Compassmate.model.ReportTrip',
        'Compassmate.view.dialog.ReportConfig',
        'Compassmate.store.ReportEventTypes'
    ],

    config: {
        listen: {
            controller: {
                '*': {
                    selectdevice: 'selectDevice',
                    showsingleevent: 'showSingleEvent',
                    deselectfeature: 'deselectFeature'
                },
                'map': {
                    selectreport: 'selectReport'
                }
            },
            store: {
                '#ReportEvents': {
                    add: 'loadRelatedPositions',
                    load: 'loadRelatedPositions'
                },
                '#ReportRoute': {
                    load: 'loadRoute'
                },
                '#ReportStops': {
                    load: 'loadRelatedPositions'
                }
            }
        }
    },

    hideReports: function () {
        Compassmate.app.showReports(false);
    },

    getGrid: function () {
        return this.getView().getComponent('grid');
    },

    getChart: function () {
        return this.getView().getComponent('chart');
    },

    init: function () {
        var i, data, attribute;
        data = Ext.getStore('PositionAttributes').getData().items;
        for (i = 0; i < data.length; i++) {
            attribute = data[i];
            this.routeColumns.push({
                text: attribute.get('name'),
                dataIndex: 'attribute.' + attribute.get('key'),
                renderer: Compassmate.AttributeFormatter.getAttributeFormatter(attribute.get('key')),
                hidden: true
            });
        }
        if (Compassmate.app.getVehicleFeaturesDisabled()) {
            for (i = 0; i < this.summaryColumns.length; i++) {
                if (this.summaryColumns[i].dataIndex.match('engineHours|spentFuel')) {
                    this.summaryColumns[i].hidden = true;
                }
            }
            for (i = 0; i < this.tripsColumns.length; i++) {
                if (this.tripsColumns[i].dataIndex.match('spentFuel|driverUniqueId')) {
                    this.tripsColumns[i].hidden = true;
                }
            }
            for (i = 0; i < this.stopsColumns.length; i++) {
                if (this.stopsColumns[i].dataIndex.match('engineHours|spentFuel')) {
                    this.stopsColumns[i].hidden = true;
                }
            }
        }
    },

    onConfigureClick: function () {
        var dialog = Ext.create('Compassmate.view.dialog.ReportConfig');
        dialog.lookupReference('eventTypeField').setHidden(this.lookupReference('reportTypeField').getValue() !== 'events');
        dialog.lookupReference('chartTypeField').setHidden(this.lookupReference('reportTypeField').getValue() !== 'chart');
        if (this.lookupReference('reportTypeField').getValue() === 'geofence') {
            dialog.lookupReference('eventTypeField').setHidden(true);
            dialog.lookupReference('chartTypeField').setHidden(true);
            dialog.lookupReference('geofenceField').setHidden(false);
            dialog.lookupReference('deviceField').setHidden(false);
            dialog.lookupReference('showMarkersField').setHidden(true);
//          dialog.lookupReference('fromDateContainerField').setHidden(true);
//          dialog.lookupReference('ContainerField').setFieldLabel(Strings.reportWhen);
        }
        dialog.callingPanel = this;
        dialog.lookupReference('deviceField').setValue(this.deviceId);
        dialog.lookupReference('groupField').setValue(this.groupId);
        if (this.eventType !== undefined) {
            dialog.lookupReference('eventTypeField').setValue(this.eventType);
        } else {
            dialog.lookupReference('eventTypeField').setValue([Compassmate.store.ReportEventTypes.allEvents]);
        }
        if (this.chartType !== undefined) {
            dialog.lookupReference('chartTypeField').setValue(this.chartType);
        }
        if (this.showMarkers !== undefined) {
            dialog.lookupReference('showMarkersField').setValue(this.showMarkers);
        }
        if (this.fromDate !== undefined) {
            dialog.lookupReference('fromDateField').setValue(this.fromDate);
        }
        if (this.fromTime !== undefined) {
            dialog.lookupReference('fromTimeField').setValue(this.fromTime);
        }
        if (this.toDate !== undefined) {
            dialog.lookupReference('toDateField').setValue(this.toDate);
        }
        if (this.toTime !== undefined) {
            dialog.lookupReference('toTimeField').setValue(this.toTime);
        }
        dialog.show();
    },

    updateButtons: function () {
        var reportType, disabled, devices, time;
        reportType = this.lookupReference('reportTypeField').getValue();
        devices = this.deviceId && this.deviceId.length !== 0 || this.groupId && this.groupId.length !== 0;
        time = this.fromDate && this.fromTime && this.toDate && this.toTime;
        disabled = !reportType || !devices || !time;
        this.lookupReference('showButton').setDisabled(reportType === '' || disabled);
        this.lookupReference('exportButton').setDisabled(reportType === 'chart' || disabled);
    },

    onReportClick: function (button) {
        var reportType, from, to, store, url;

        this.getGrid().getSelectionModel().deselectAll();

        reportType = this.lookupReference('reportTypeField').getValue();
        if (reportType && (this.deviceId || this.groupId)) {
            from = new Date(
                this.fromDate.getFullYear(), this.fromDate.getMonth(), this.fromDate.getDate(),
                this.fromTime.getHours(), this.fromTime.getMinutes(), this.fromTime.getSeconds(), this.fromTime.getMilliseconds());

            to = new Date(
                this.toDate.getFullYear(), this.toDate.getMonth(), this.toDate.getDate(),
                this.toTime.getHours(), this.toTime.getMinutes(), this.toTime.getSeconds(), this.toTime.getMilliseconds());

            if (button.reference === 'showButton') {
                if (reportType === 'chart') {
                    store = this.getChart().getStore();
                    this.getChart().setSeries([]);
                } else {
                    store = this.getGrid().getStore();
                }
                store.showMarkers = this.showMarkers;
                store.load({
                    params: {
                        deviceId: this.deviceId,
                        groupId: this.groupId,
                        geofenceId: this.geofenceId,
                        type: this.eventType,
                        from: from.toISOString(),
                        to: to.toISOString()
                    }
                });
            } else if (button.reference === 'exportButton') {
                url = this.getGrid().getStore().getProxy().url;
                this.downloadFile(url, {
                    deviceId: this.deviceId,
                    groupId: this.groupId,
                    geofenceId: this.geofenceId,
                    type: this.eventType,
                    from: Ext.Date.format(from, 'c'),
                    to: Ext.Date.format(to, 'c')
                });
            }
        }
    },

    onClearClick: function () {
        var reportType = this.lookupReference('reportTypeField').getValue();
        this.clearReport(reportType);
    },

    clearReport: function (reportType) {
        this.getGrid().getStore().removeAll();
        if (reportType === 'trips' || reportType === 'events' || reportType === 'stops') {
            Ext.getStore('ReportRoute').removeAll();
        }
        if (reportType === 'chart') {
            this.getChart().getStore().removeAll();
        }
    },

    onSelectionChange: function (selection, selected) {
        var report;
        if (selected.length > 0) {
            report = selected[0];
            this.fireEvent('selectreport', report, true);
            if (report instanceof Compassmate.model.ReportTrip) {
                this.selectTrip(report);
            }
            if (report instanceof Compassmate.model.Event || report instanceof Compassmate.model.ReportStop) {
                this.selectPositionRelated(report);
            }
        }
    },

    selectDevice: function (device) {
        if (device) {
            this.getGrid().getSelectionModel().deselectAll();
        }
    },

    selectReport: function (object) {
        var positionRelated, reportType = this.lookupReference('reportTypeField').getValue();
        if (object instanceof Compassmate.model.Position) {
            if (reportType === 'route') {
                this.getGrid().getSelectionModel().select([object], false, true);
                this.getGrid().getView().focusRow(object);
            } else if (reportType === 'events' || reportType === 'stops') {
                positionRelated = this.getGrid().getStore().findRecord('positionId', object.get('id'), 0, false, true, true);
                this.getGrid().getSelectionModel().select([positionRelated], false, true);
                this.getGrid().getView().focusRow(positionRelated);
            }
        }
    },

    deselectFeature: function () {
        if (this.lookupReference('reportTypeField').getValue() !== 'trips') {
            this.getGrid().getSelectionModel().deselectAll();
        }
    },

    selectTrip: function (trip) {
        var from, to;
        from = new Date(trip.get('startTime'));
        to = new Date(trip.get('endTime'));
        Ext.getStore('ReportRoute').removeAll();
        Ext.getStore('ReportRoute').showMarkers = this.showMarkers;
        Ext.getStore('ReportRoute').load({
            params: {
                deviceId: trip.get('deviceId'),
                from: from.toISOString(),
                to: to.toISOString()
            }
        });
    },

    selectPositionRelated: function (report) {
        var position;
        if (report.get('positionId')) {
            position = Ext.getStore('ReportRoute').getById(report.get('positionId'));
            if (position) {
                this.fireEvent('selectreport', position, true);
            }
        }
    },

    loadRelatedPositions: function (store, data) {
        var i, reportObject, positionIds = [];
        Ext.getStore('ReportRoute').removeAll();
        for (i = 0; i < data.length; i++) {
            reportObject = data[i];
            if (reportObject.get('positionId')) {
                positionIds.push(reportObject.get('positionId'));
            }
        }
        if (positionIds.length > 0) {
            Ext.getStore('Positions').load({
                params: {
                    id: positionIds
                },
                scope: this,
                callback: function (records, operation, success) {
                    if (success) {
                        Ext.getStore('ReportRoute').showMarkers = this.showMarkers;
                        Ext.getStore('ReportRoute').add(records);
                        if (records.length === 1) {
                            this.fireEvent('selectreport', records[0], false);
                        }
                    }
                }
            });
        }
    },

    loadRoute: function (store) {
        var i, deviceIds, chartSeries, deviceStore;
        if (this.lookupReference('reportTypeField').getValue() === 'chart') {
            this.getChart().getAxes()[0].setTitle(
                Ext.getStore('ReportChartTypes').findRecord('key', this.chartType).get('name'));
            chartSeries = [];
            deviceIds = store.collect('deviceId');
            for (i = 0; i < deviceIds.length; i++) {
                deviceStore = Ext.create('Ext.data.ChainedStore', {
                    source: 'ReportRoute',
                    filters: [{
                        property: 'deviceId',
                        value: deviceIds[i]
                    }]
                });
                chartSeries.push({
                    type: 'line',
                    store: deviceStore,
                    yField: this.chartType,
                    xField: 'fixTime',
                    highlightCfg: {
                        scaling: Compassmate.Style.chartMarkerHighlightScaling
                    },
                    colors: [Compassmate.app.getReportColor(deviceIds[i])],
                    marker: {
                        type: 'circle',
                        radius: Compassmate.Style.chartMarkerRadius,
                        fill: Compassmate.app.getReportColor(deviceIds[i])
                    }
                });
            }
            this.getChart().setSeries(chartSeries);
        }
    },

    onChartMarkerClick: function (chart, item) {
        this.fireEvent('selectreport', item.record, true);
    },

    showSingleEvent: function (eventId) {
        this.lookupReference('reportTypeField').setValue('events');
        Ext.getStore('Events').load({
            id: eventId,
            scope: this,
            callback: function (records, operation, success) {
                if (success) {
                    Ext.getStore('ReportEvents').add(records);
                    if (records.length > 0) {
                        if (!records[0].get('positionId')) {
                            if (Compassmate.app.isMobile()) {
                                Compassmate.app.showReports(true);
                            } else {
                                this.getView().expand();
                            }
                        }
                        this.getGrid().getSelectionModel().select([records[0]], false, true);
                        this.getGrid().getView().focusRow(records[0]);
                    }
                }
            }
        });
    },

    downloadFile: function (requestUrl, requestParams) {
    	Ext.MessageBox.show({
            msg : Strings.sharedPleaseWait,
            progressText : Strings.reportGenerating,
            width : 300,
            wait : true,
            waitConfig : 
            {
                increment : 15,
                text : Strings.reportGenerating,
                scope : this,
                fn : function(){
                    
                }
            }
        });
        Ext.Ajax.request({
            url: requestUrl,
            method: 'GET',
            params: requestParams,
            headers: {
                Accept: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
            },
            binary: true,
            success: function (response) {
                var disposition, filename, type, blob, url, downloadUrl;
                disposition = response.getResponseHeader('Content-Disposition');
                filename = disposition.slice(disposition.indexOf('=') + 1, disposition.length);
                type = response.getResponseHeader('Content-Type');
                blob = new Blob([response.responseBytes], {type: type});
                if (typeof window.navigator.msSaveBlob !== 'undefined') {
                    // IE workaround
                    window.navigator.msSaveBlob(blob, filename);
                } else {
                    url = window.URL || window.webkitURL;
                    downloadUrl = url.createObjectURL(blob);
                    if (filename) {
                    	Ext.MessageBox.hide();
                        Ext.dom.Helper.append(Ext.getBody(), {
                            tag: 'a',
                            href: downloadUrl,
                            download: filename
                        }).click();
                    }
                    setTimeout(function () {
                        url.revokeObjectURL(downloadUrl);
                    }, 100);
                }
            }
        });
    },

    onTypeChange: function (combobox, newValue, oldValue) {
        if (oldValue !== null) {
            this.clearReport(oldValue);
        }

        if (newValue === 'route') {
            this.getGrid().reconfigure('ReportRoute', this.routeColumns);
            this.getView().getLayout().setActiveItem('grid');
        } else if (newValue === 'events') {
            this.getGrid().reconfigure('ReportEvents', this.eventsColumns);
            this.getView().getLayout().setActiveItem('grid');
        } else if (newValue === 'summary') {
            this.getGrid().reconfigure('ReportSummary', this.summaryColumns);
            this.getView().getLayout().setActiveItem('grid');
        } else if (newValue === 'trips') {
            this.getGrid().reconfigure('ReportTrips', this.tripsColumns);
            this.getView().getLayout().setActiveItem('grid');
        } else if (newValue === 'stops') {
            this.getGrid().reconfigure('ReportStops', this.stopsColumns);
            this.getView().getLayout().setActiveItem('grid');
        } else if (newValue === 'chart') {
            this.getView().getLayout().setActiveItem('chart');
        } else if (newValue === 'geofence') {
            this.getGrid().reconfigure('ReportGeofence', this.geofenceColumns);
        } else if (newValue === 'fuel') {
	    this.getGrid().reconfigure('ReportFuel', this.fuelColumns);
	    this.getView().getLayout().setActiveItem('grid');
	} else if (newValue ==='daily') {
	    this.getGrid().reconfigure('ReportDaily', this.resCountColumns);
	    this.getView().getLayout().setActiveItem('grid');
	}

        this.updateButtons();
    },
    
    resCountColumns: [{
    	text: Strings.reportDeviceName,
    	dataIndex: 'deviceId',
    	renderer: Compassmate.AttributeFormatter.getFormatter('deviceId')
    },
    {
    	text: "Res count",
    	dataIndex: 'resCount',
    	renderer: Compassmate.AttributeFormatter.getFormatter('resCount')
   
    },{
    	text: "Start time",
    	dataIndex: 'startTime',
    	renderer: Compassmate.AttributeFormatter.getFormatter('startTime')
    },
    {
    	text: "End time",
    	dataIndex: 'endTime',
    	renderer: Compassmate.AttributeFormatter.getFormatter('endTime')
    },
    {
    	text: "Duration",
    	dataIndex: 'resDuration',
    	renderer: Compassmate.AttributeFormatter.getFormatter('resDuration')
    }],
    
    routeColumns: [{
        text: Strings.reportDeviceName,
        dataIndex: 'deviceId',
        renderer: Compassmate.AttributeFormatter.getFormatter('deviceId')
    }, {
        text: Strings.positionValid,
        dataIndex: 'valid',
        renderer: Compassmate.AttributeFormatter.getFormatter('valid')
    }, {
        text: Strings.positionFixTime,
        dataIndex: 'fixTime',
        xtype: 'datecolumn',
        renderer: Compassmate.AttributeFormatter.getFormatter('fixTime')
    }, {
        text: Strings.positionLatitude,
        dataIndex: 'latitude',
        renderer: Compassmate.AttributeFormatter.getFormatter('latitude')
    }, {
        text: Strings.positionLongitude,
        dataIndex: 'longitude',
        renderer: Compassmate.AttributeFormatter.getFormatter('longitude')
    }, {
        text: Strings.positionAltitude,
        dataIndex: 'altitude',
        renderer: Compassmate.AttributeFormatter.getFormatter('altitude')
    }, {
        text: Strings.positionSpeed,
        dataIndex: 'speed',
        renderer: Compassmate.AttributeFormatter.getFormatter('speed')
    }, {
        text: Strings.positionAddress,
        dataIndex: 'address',
        renderer: Compassmate.AttributeFormatter.getFormatter('address')
    }],

    eventsColumns: [{
        text: Strings.positionFixTime,
        dataIndex: 'serverTime',
        xtype: 'datecolumn',
        renderer: Compassmate.AttributeFormatter.getFormatter('serverTime')
    }, {
        text: Strings.reportDeviceName,
        dataIndex: 'deviceId',
        renderer: Compassmate.AttributeFormatter.getFormatter('deviceId')
    }, {
        text: Strings.sharedType,
        dataIndex: 'type',
        renderer: function (value) {
            return Compassmate.app.getEventString(value);
        }
    }/*, {
        text: Strings.sharedGeofence,
        dataIndex: 'geofenceId',
        renderer: Compassmate.AttributeFormatter.getFormatter('geofenceId')
    }*/],

    summaryColumns: [{
        text: Strings.reportDeviceName,
        dataIndex: 'deviceId',
        renderer: Compassmate.AttributeFormatter.getFormatter('deviceId')
    }, {
        text: Strings.sharedDistance,
        dataIndex: 'distance',
        renderer: Compassmate.AttributeFormatter.getFormatter('distance')
    }, {
        text: Strings.reportAverageSpeed,
        dataIndex: 'averageSpeed',
        renderer: Compassmate.AttributeFormatter.getFormatter('speed')
    }, {
        text: Strings.reportMaximumSpeed,
        dataIndex: 'maxSpeed',
        renderer: Compassmate.AttributeFormatter.getFormatter('speed')
    },
    {
    	text: "Overspeeding distance",
    	dataIndex: 'overSpeedDistance',
    	renderer: Compassmate.AttributeFormatter.getFormatter('overSpeedDistance')
    },
    {
    	text: "activeStopTime",
    	dataIndex: 'activeStopTime',
    	renderer: Compassmate.AttributeFormatter.getFormatter('activeStopTime')
    },
    {
    	text: "activeMoveTime  ",
    	dataIndex: 'activeMoveTime',
    	renderer: Compassmate.AttributeFormatter.getFormatter('activeMoveTime')
    },
    {
    	text: "Res count",
    	dataIndex: 'resCount',
    	renderer: Compassmate.AttributeFormatter.getFormatter('resCount')

    
    }/*,{
        text: Strings.reportEngineHours,
        dataIndex: 'engineHours',
        renderer: Compassmate.AttributeFormatter.getFormatter('duration')
    }, {
        text: Strings.reportSpentFuel,
        dataIndex: 'spentFuel',
        renderer: Compassmate.AttributeFormatter.getFormatter('spentFuel')
    }*/],
  
    tripsColumns: [{
        text: Strings.reportDeviceName,
        dataIndex: 'deviceId',
        renderer: Compassmate.AttributeFormatter.getFormatter('deviceId')
    }, {
        text: Strings.reportStartTime,
        dataIndex: 'startTime',
        xtype: 'datecolumn',
        renderer: Compassmate.AttributeFormatter.getFormatter('startTime')
    }, {
        text: Strings.reportStartAddress,
        dataIndex: 'startAddress',
        renderer: Compassmate.AttributeFormatter.getFormatter('address')
    }, {
        text: Strings.reportEndTime,
        dataIndex: 'endTime',
        xtype: 'datecolumn',
        renderer: Compassmate.AttributeFormatter.getFormatter('endTime')
    }, {
        text: Strings.reportEndAddress,
        dataIndex: 'endAddress',
        renderer: Compassmate.AttributeFormatter.getFormatter('address')
    }, {
        text: Strings.sharedDistance,
        dataIndex: 'distance',
        renderer: Compassmate.AttributeFormatter.getFormatter('distance')
    }, {
        text: Strings.reportAverageSpeed,
        dataIndex: 'averageSpeed',
        renderer: Compassmate.AttributeFormatter.getFormatter('speed')
    }, {
        text: Strings.reportMaximumSpeed,
        dataIndex: 'maxSpeed',
        renderer: Compassmate.AttributeFormatter.getFormatter('speed')
    }, {
        text: Strings.reportDuration,
        dataIndex: 'duration',
        renderer: Compassmate.AttributeFormatter.getFormatter('duration')
    }, /*{
        text: Strings.reportSpentFuel,
        dataIndex: 'spentFuel',
        renderer: Compassmate.AttributeFormatter.getFormatter('spentFuel')
    },*/ {
        text: Strings.sharedDriver,
        dataIndex: 'driverUniqueId',
        renderer: Compassmate.AttributeFormatter.getFormatter('driverUniqueId')
    }],

    stopsColumns: [{
        text: Strings.reportDeviceName,
        dataIndex: 'deviceId',
        renderer: Compassmate.AttributeFormatter.getFormatter('deviceId')
    }, {
        text: Strings.reportStartTime,
        dataIndex: 'startTime',
        xtype: 'datecolumn',
        renderer: Compassmate.AttributeFormatter.getFormatter('startTime')
    }, {
        text: Strings.positionAddress,
        dataIndex: 'address',
        renderer: Compassmate.AttributeFormatter.getFormatter('address')
    }, {
        text: Strings.reportEndTime,
        dataIndex: 'endTime',
        xtype: 'datecolumn',
        renderer: Compassmate.AttributeFormatter.getFormatter('endTime')
    }, {
        text: Strings.reportDuration,
        dataIndex: 'duration',
        renderer: Compassmate.AttributeFormatter.getFormatter('duration')
    }, {
        text: Strings.reportEngineHours,
        dataIndex: 'engineHours',
        renderer: Compassmate.AttributeFormatter.getFormatter('duration')
    }/*, {
        text: Strings.reportSpentFuel,
        dataIndex: 'spentFuel',
        renderer: Compassmate.AttributeFormatter.getFormatter('spentFuel')
    }*/],

    fuelColumns: [{
        text: Strings.reportDeviceName,
        dataIndex: 'deviceId',
        renderer: Compassmate.AttributeFormatter.getFormatter('deviceId')
    }, {
        text: Strings.reportFirstFuelVelocity,
        dataIndex: 'firstFuelVelocity',
        renderer: Compassmate.AttributeFormatter.getFormatter('firstFuelVelocity')
    }, {
        text: Strings.reportChargedFuel,
        dataIndex: 'chargedFuel',
        renderer: Compassmate.AttributeFormatter.getFormatter('chargedFuel')
    }, {
        text: Strings.reportSpentFuel,
        dataIndex: 'spentFuel',
        renderer: Compassmate.AttributeFormatter.getFormatter('spentFuel')
    }, {
        text: Strings.reportLastFuelVelocity,
        dataIndex: 'lastFuelVelocity',
        renderer: Compassmate.AttributeFormatter.getFormatter('lastFuelVelocity')
    }, {
        text: Strings.reportChargedCount,
        dataIndex: 'chargedCount'
    }],

    geofenceColumns: [{
        text: Strings.sharedGeofence,
        dataIndex: 'geofenceName',
        renderer: Compassmate.AttributeFormatter.getFormatter("groupId")
    },/* {
        text: Strings.reportGroup,
        dataIndex: 'groupName',
        renderer: Compassmate.AttributeFormatter.getFormatter("groupId")
    }, */{
        text: Strings.reportDevice,
        dataIndex: 'deviceName',
        renderer: Compassmate.AttributeFormatter.getFormatter("groupId")
    }, {
        text: Strings.reportEntryDate,
        dataIndex: 'entryDate',
        xtype: 'datecolumn',
        renderer: Compassmate.AttributeFormatter.getFormatter("entryDate")
    }, {
        text: Strings.reportExitDate,
        dataIndex: 'exitDate',
        xtype: 'datecolumn',
        renderer: Compassmate.AttributeFormatter.getFormatter("exitDate")
    },/* {
        text: Strings.sharedDistance,
        dataIndex: 'distance',
        renderer: Compassmate.AttributeFormatter.getFormatter("distance")
    }, */{
        text: Strings.reportSpentTime,
        dataIndex: 'duration',
        renderer: Compassmate.AttributeFormatter.getFormatter("geofenceDuration")
    } /*,{
        text: Strings.reportSpentFuel,
        dataIndex: 'spentFuel'
    }, {
        text: Strings.reportStopDuration,
        dataIndex: 'stopDuration',
        renderer: Compassmate.AttributeFormatter.getFormatter("geofenceDuration")
    }, {
        text: Strings.reportMotionDuration,
        dataIndex: 'motionDuration',
        renderer: Compassmate.AttributeFormatter.getFormatter("geofenceDuration")
    }*/]
});
