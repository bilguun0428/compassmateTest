package mn.compassmate.api.resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import mn.compassmate.api.BaseResource;
import mn.compassmate.helper.DateUtil;
import mn.compassmate.model.Event;
import mn.compassmate.model.Position;
import mn.compassmate.reports.Daily;
import mn.compassmate.reports.Events;
import mn.compassmate.reports.Fuel;
import mn.compassmate.reports.Geofence;
import mn.compassmate.reports.Summary;
import mn.compassmate.reports.Trips;
import mn.compassmate.reports.model.StopReport;
import mn.compassmate.reports.model.SummaryReport;
import mn.compassmate.reports.model.TripReport;
import mn.compassmate.reports.Route;
import mn.compassmate.reports.Stops;
import mn.compassmate.reports.model.FuelReport;
import mn.compassmate.reports.model.GeofenceReport;

@Path("reports")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReportResource extends BaseResource {

    private static final String XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final String CONTENT_DISPOSITION_VALUE_XLSX = "attachment; filename=report.xlsx";

    @Path("route")
    @GET
    public Collection<Position> getRoute(
            @QueryParam("deviceId") final List<Long> deviceIds, @QueryParam("groupId") final List<Long> groupIds,
            @QueryParam("from") String from, @QueryParam("to") String to) throws InterruptedException {
        return Route.getObjects(getUserId(), deviceIds, groupIds,
                DateUtil.parseDate(from), DateUtil.parseDate(to));
    }

    @Path("route")
    @GET
    @Produces(XLSX)
    public Response getRouteExcel(
            @QueryParam("deviceId") final List<Long> deviceIds, @QueryParam("groupId") final List<Long> groupIds,
            @QueryParam("from") String from, @QueryParam("to") String to) throws SQLException, IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Route.getExcel(stream, getUserId(), deviceIds, groupIds,
                DateUtil.parseDate(from), DateUtil.parseDate(to));

        return Response.ok(stream.toByteArray())
                .header(HttpHeaders.CONTENT_DISPOSITION, CONTENT_DISPOSITION_VALUE_XLSX).build();
    }

    @Path("events")
    @GET
    public Collection<Event> getEvents(
            @QueryParam("deviceId") final List<Long> deviceIds, @QueryParam("groupId") final List<Long> groupIds,
            @QueryParam("type") final List<String> types,
            @QueryParam("from") String from, @QueryParam("to") String to) throws SQLException {
        return Events.getObjects(getUserId(), deviceIds, groupIds, types,
                DateUtil.parseDate(from), DateUtil.parseDate(to));
    }

    @Path("events")
    @GET
    @Produces(XLSX)
    public Response getEventsExcel(
            @QueryParam("deviceId") final List<Long> deviceIds, @QueryParam("groupId") final List<Long> groupIds,
            @QueryParam("type") final List<String> types,
            @QueryParam("from") String from, @QueryParam("to") String to) throws SQLException, IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Events.getExcel(stream, getUserId(), deviceIds, groupIds, types,
                DateUtil.parseDate(from), DateUtil.parseDate(to));

        return Response.ok(stream.toByteArray())
                .header(HttpHeaders.CONTENT_DISPOSITION, CONTENT_DISPOSITION_VALUE_XLSX).build();
    }

    @Path("summary")
    @GET
    public Collection<SummaryReport> getSummary(
            @QueryParam("deviceId") final List<Long> deviceIds, @QueryParam("groupId") final List<Long> groupIds,
            @QueryParam("from") String from, @QueryParam("to") String to) throws InterruptedException {
        return Summary.getObjects(getUserId(), deviceIds, groupIds,
                DateUtil.parseDate(from), DateUtil.parseDate(to));
    }

    @Path("summary")
    @GET
    @Produces(XLSX)
    public Response getSummaryExcel(
            @QueryParam("deviceId") final List<Long> deviceIds, @QueryParam("groupId") final List<Long> groupIds,
            @QueryParam("from") String from, @QueryParam("to") String to) throws IOException, InterruptedException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Summary.getExcel(stream, getUserId(), deviceIds, groupIds,
                DateUtil.parseDate(from), DateUtil.parseDate(to));

        return Response.ok(stream.toByteArray())
                .header(HttpHeaders.CONTENT_DISPOSITION, CONTENT_DISPOSITION_VALUE_XLSX).build();
    }

    @Path("trips")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<TripReport> getTrips(
            @QueryParam("deviceId") final List<Long> deviceIds, @QueryParam("groupId") final List<Long> groupIds,
            @QueryParam("from") String from, @QueryParam("to") String to) throws InterruptedException {
        return Trips.getObjects(getUserId(), deviceIds, groupIds,
                DateUtil.parseDate(from), DateUtil.parseDate(to));
    }

    @Path("trips")
    @GET
    @Produces(XLSX)
    public Response getTripsExcel(
            @QueryParam("deviceId") final List<Long> deviceIds, @QueryParam("groupId") final List<Long> groupIds,
            @QueryParam("from") String from, @QueryParam("to") String to) throws IOException, InterruptedException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Trips.getExcel(stream, getUserId(), deviceIds, groupIds,
                DateUtil.parseDate(from), DateUtil.parseDate(to));

        return Response.ok(stream.toByteArray())
                .header(HttpHeaders.CONTENT_DISPOSITION, CONTENT_DISPOSITION_VALUE_XLSX).build();
    }

    @Path("stops")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<StopReport> getStops(
            @QueryParam("deviceId") final List<Long> deviceIds, @QueryParam("groupId") final List<Long> groupIds,
            @QueryParam("from") String from, @QueryParam("to") String to) throws InterruptedException {
        return Stops.getObjects(getUserId(), deviceIds, groupIds,
                DateUtil.parseDate(from), DateUtil.parseDate(to));
    }

    @Path("stops")
    @GET
    @Produces(XLSX)
    public Response getStopsExcel(
            @QueryParam("deviceId") final List<Long> deviceIds, @QueryParam("groupId") final List<Long> groupIds,
            @QueryParam("from") String from, @QueryParam("to") String to) throws IOException, InterruptedException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Stops.getExcel(stream, getUserId(), deviceIds, groupIds,
                DateUtil.parseDate(from), DateUtil.parseDate(to));

        return Response.ok(stream.toByteArray())
                .header(HttpHeaders.CONTENT_DISPOSITION, CONTENT_DISPOSITION_VALUE_XLSX).build();
    }

    @Path("fuel")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<FuelReport> getFuel(
            @QueryParam("deviceId") final List<Long> deviceIds, @QueryParam("groupId") final List<Long> groupIds,
            @QueryParam("from") String from, @QueryParam("to") String to) throws InterruptedException {
        return Fuel.getObjects(getUserId(), deviceIds, groupIds, DateUtil.parseDate(from), DateUtil.parseDate(to));
    }

    @Path("fuel")
    @GET
    @Produces(XLSX)
    public Response getFuelExcel(
            @QueryParam("deviceId") final List<Long> deviceIds, @QueryParam("groupId") final List<Long> groupIds, 
            @QueryParam("from") String from, @QueryParam("to") String to) throws IOException, InterruptedException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Fuel.getExcel(stream, getUserId(), deviceIds, groupIds,
                DateUtil.parseDate(from), DateUtil.parseDate(to));
        return Response.ok(stream.toByteArray())
                .header(HttpHeaders.CONTENT_DISPOSITION, CONTENT_DISPOSITION_VALUE_XLSX).build();
    }

    @Path("daily")
    @GET
    @Produces(XLSX)
    public Response getDaily(
            @QueryParam("deviceId") final List<Long> deviceIds, @QueryParam("groupId") final List<Long> groupIds,
            @QueryParam("from") String from, @QueryParam("to") String to) throws IOException, InterruptedException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Daily.getExcel(stream, getUserId(), deviceIds, groupIds,
                DateUtil.parseDate(from), DateUtil.parseDate(to));
        return Response.ok(stream.toByteArray())
                .header(HttpHeaders.CONTENT_DISPOSITION, CONTENT_DISPOSITION_VALUE_XLSX).build();
    }

    @Path("geofence")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public  Collection<GeofenceReport> getGeofence(
            @QueryParam("deviceId") final List<Long> deviceIds, @QueryParam("groupId") final List<Long> groupIds,
            @QueryParam("geofenceId") final List<Long> geofenceIds,
            @QueryParam("from") String from, @QueryParam("to") String to) throws SQLException, IOException {
        return Geofence.getObjects(getUserId(), deviceIds, groupIds, geofenceIds, DateUtil.parseDate(from),
               DateUtil.parseDate(to));
    }

    @Path("geofence")
    @GET
    @Produces(XLSX)
    public  Response getGeofenceExcel(
            @QueryParam("deviceId") final List<Long> deviceIds, @QueryParam("groupId") final List<Long> groupIds,
            @QueryParam("geofenceId") final List<Long> geofenceIds,
            @QueryParam("from") String from, @QueryParam("to") String to) throws SQLException, IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Geofence.getExcel(stream, getUserId(), deviceIds, groupIds, geofenceIds,
                DateUtil.parseDate(from), DateUtil.parseDate(to));
        return Response.ok(stream.toByteArray())
                .header(HttpHeaders.CONTENT_DISPOSITION, CONTENT_DISPOSITION_VALUE_XLSX).build();
    }
}
