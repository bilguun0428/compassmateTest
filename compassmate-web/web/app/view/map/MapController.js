 

Ext.define('Compassmate.view.map.MapController', {
    extend: 'Compassmate.view.map.MapMarkerController',
    alias: 'controller.map',

    requires: [
        'Compassmate.GeofenceConverter',
        'Compassmate.view.BaseWindow',
        'Compassmate.view.map.GeofenceMap'
    ],

    config: {
        listen: {
            controller: {
                '*': {
                    mapstaterequest: 'getMapState',
                    zoomtoalldevices: 'zoomToAllDevices'
                }
            },
            store: {
                '#Geofences': {
                    load: 'updateGeofences',
                    add: 'updateGeofences',
                    update: 'updateGeofences',
                    remove: 'updateGeofences'
                }
            }
        }
    },

    init: function () {
        this.callParent();
        this.lookupReference('showReportsButton').setVisible(
            Compassmate.app.isMobile() && !Compassmate.app.getBooleanAttributePreference('ui.disableReport'));
        this.lookupReference('showEventsButton').setVisible(Compassmate.app.isMobile());
    },

    showReports: function () {
        Compassmate.app.showReports(true);
    },

    showEvents: function () {
        Compassmate.app.showEvents(true);
    },

    onFollowClick: function (button, pressed) {
        if (pressed && this.selectedMarker) {
            this.getView().getMapView().setCenter(this.selectedMarker.getGeometry().getCoordinates());
        }
    },

    showLiveRoutes: function (button) {
        this.getView().getLiveRouteLayer().setVisible(button.pressed);
       },
    
    onKeyPress: function (textfield,eo){
        if (eo.getCharCode() == Ext.EventObject.ENTER) {
        	var lon, lat
        	var map = this.getView().getMap();
            lon = textfield.value.split(',')[0]
        	lat = textfield.value.split(',')[1]
        	this.getView().getMapView().setCenter(ol.proj.fromLonLat([lon, Math.abs(lat)]))

        	var geometry, marker, style, point = ol.proj.fromLonLat([
                lon, Math.abs(lat)
            ]);
            geometry = new ol.geom.Point(point);
            marker = new ol.Feature(geometry);
            
            var iconStyle = new ol.style.Style({
                image: new ol.style.Icon(/** @type {module:ol/style/Icon~Options} */ ({
                  anchor: [0.5, 46],
                  anchorXUnits: 'fraction',
                  anchorYUnits: 'pixels',
                  src: 'images/placeholder.png'
                }))
              });
            marker.setStyle(iconStyle);
            var features = this.getView().getMarkersSource().getFeatures()
            for (var i = 0; i < features.length; i++){
            	this.getView().getMarkersSource().removeFeature(features[i])
//            	console.log('i')
            }
            this.getView().getMarkersSource().addFeature(marker)
        }
     },

	initialize: function(latitude,longitude) {
		var latlng = new google.maps.LatLng(latitude,longitude);

		var myOptions = {
				zoom: 14,
				center: latlng,
				mapTypeId: google.maps.MapTypeId.ROADMAP,
				mapTypeControl: false
		};
		var map = new google.maps.Map(document.getElementById("map_canvas"),myOptions);

		var marker = new google.maps.Marker({
			position: latlng,
			map: map, 
			title:"location : Dublin"
		});     
    },
    onTypeChange: function (combobox,event) {
    	var mapType = combobox.value;
    	var map = this.getView().getMap();
    	var layer = this.getView().superclass.getLayer(mapType)
    	var layers = map.getLayers().array_;
    	for (var i = 0; i < layers.length; i++) {
//    		console.log(layers[i], layers[i].getZIndex())
			if(layers[i] instanceof ol.layer.Tile) {
	//    			console.log('remove', layers[i].get('name'))
				map.removeLayer(layers[i])
			}
    	}
    	map.addLayer(layer)
    	layer.setZIndex(-1)
    	console.log(layers)
    },
    getMapState: function () {
        var zoom, center, projection;
        projection = this.getView().getMapView().getProjection();
        center = ol.proj.transform(this.getView().getMapView().getCenter(), projection, 'EPSG:4326');
        zoom = this.getView().getMapView().getZoom();
        this.fireEvent('mapstate', center[1], center[0], zoom);
    },
    onGeofencesClick: function () {
        Ext.create('Compassmate.view.BaseWindow', {
        	title: Strings.sharedGeofences,
            width: 300,
            height: 360,
            x: 1010,
            y: 40,
            items: {
                xtype: 'geofencesView',
            }
        }).show();
    },
    updateGeofences: function () {
        this.getView().getGeofencesSource().clear();
        if (this.lookupReference('showGeofencesButton').pressed) {
            Ext.getStore('Geofences').each(function (geofence) {
                var feature = new ol.Feature(
                    Compassmate.GeofenceConverter.wktToGeometry(this.getView().getMapView(), geofence.get('area')));
                feature.setStyle(this.getAreaStyle(
                    geofence.get('name'), geofence.get('attributes') ? geofence.get('attributes').color : null));
                this.getView().getGeofencesSource().addFeature(feature);
                return true;
            }, this);
        }
    },

    zoomToAllDevices: function () {
        this.zoomToAllPositions(Ext.getStore('LatestPositions').getData().items);
    }
});
