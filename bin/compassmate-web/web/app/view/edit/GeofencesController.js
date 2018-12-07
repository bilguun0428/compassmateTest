 

Ext.define('Compassmate.view.edit.GeofencesController', {
    extend: 'Compassmate.view.edit.ToolbarController',
    alias: 'controller.geofences',

    requires: [
        'Compassmate.view.dialog.Geofence',
        'Compassmate.model.Geofence'
    ],

    objectModel: 'Compassmate.model.Geofence',
    objectDialog: 'Compassmate.view.dialog.Geofence',
    removeTitle: Strings.sharedGeofence,
    
    onSelectionChange: function (selection, selected) {
    	this.callSuper(arguments);
        var mapView = Ext.ComponentQuery.query('mapView')[0];
        if(selected.length > 0) {
	        var geometry = Compassmate.GeofenceConverter.wktToGeometry(mapView.mapView, selected[0].data.area);
	        mapView.mapView.fit(geometry);
	    }
    }
});
