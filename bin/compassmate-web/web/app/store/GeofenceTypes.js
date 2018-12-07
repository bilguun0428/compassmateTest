 

Ext.define('Compassmate.store.GeofenceTypes', {
    extend: 'Ext.data.Store',
    fields: ['key', 'name'],

    data: [{
        key: 'Point',
        name: Strings.mapShapePoint
    }, {
        key: 'Polygon',
        name: Strings.mapShapePolygon
    }, {
        key: 'Circle',
        name: Strings.mapShapeCircle
    }, {
        key: 'LineString',
        name: Strings.mapShapePolyline
    }]
});
