package mn.compassmate.model;

import java.text.ParseException;

import com.fasterxml.jackson.annotation.JsonIgnore;

import mn.compassmate.Context;
import mn.compassmate.database.QueryIgnore;
import mn.compassmate.geofence.GeofenceCircle;
import mn.compassmate.geofence.GeofenceGeometry;
import mn.compassmate.geofence.GeofencePoint;
import mn.compassmate.geofence.GeofencePolygon;
import mn.compassmate.geofence.GeofencePolyline;

public class Geofence extends ExtendedModel {

    public static final String TYPE_GEOFENCE_CIRCLE = "geofenceCircle";
    public static final String TYPE_GEOFENCE_POLYGON = "geofencePolygon";
    public static final String TYPE_GEOFENCE_POLYLINE = "geofencePolyline";
    public static final String TYPE_GEOFENCE_POINT = "geofencePoint";

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String area;

    public String getArea() {
        return area;
    }

    public void setArea(String area) throws ParseException {

        if (area.startsWith("CIRCLE")) {
            geometry = new GeofenceCircle(area);
        } else if (area.startsWith("POLYGON")) {
            geometry = new GeofencePolygon(area);
        } else if (area.startsWith("LINESTRING")) {
            geometry = new GeofencePolyline(area, Context.getConfig().getDouble("geofence.polylineDistance", 25));
        } else if (area.startsWith("POINT")) {
            geometry = new GeofencePoint(area);
        } else {
            throw new ParseException("Unknown geometry type", 0);
        }

        this.area = area;
    }

    private GeofenceGeometry geometry;

    @QueryIgnore
    @JsonIgnore
    public GeofenceGeometry getGeometry() {
        return geometry;
    }

    @QueryIgnore
    @JsonIgnore
    public void setGeometry(GeofenceGeometry geometry) {
        area = geometry.toWkt();
        this.geometry = geometry;
    }

    private long calendarId;

    public long getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(long calendarId) {
        this.calendarId = calendarId;
    }
}
