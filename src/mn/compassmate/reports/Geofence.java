/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mn.compassmate.reports;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.jxls.util.JxlsHelper;

import mn.compassmate.Context;
import mn.compassmate.model.Device;
import mn.compassmate.model.Position;
import mn.compassmate.reports.model.GeofenceReport;
import mn.compassmate.reports.model.StopReport;
import mn.compassmate.reports.model.TripReport;

public final class Geofence {

    private Geofence() {
    }

    public static Collection<GeofenceReport> getObjects(long userId, Collection<Long> deviceIds,
        Collection<Long> groupIds, Collection<Long> geofenceIds, Date from, Date to) throws SQLException {
        ArrayList<GeofenceReport> ret = new ArrayList<>();
        Collection<Long> deviceList = ReportUtils.getDeviceList(deviceIds, groupIds);
        for (long geofenceId : geofenceIds) {
            mn.compassmate.model.Geofence geofence = Context.getDataManager()
                    .getObject(mn.compassmate.model.Geofence.class, geofenceId);
            GeofenceReport geofenceReport = null;
            Position firstPosition = null;
            for (long deviceId : deviceList) {
                boolean ignoreOdometer = Context.getDeviceManager()
                        .lookupAttributeBoolean(deviceId, "report.ignoreOdometer", false, true);
                double speedThreshold = Context.getConfig()
                                       .getDouble("event.motion.speedThreshold", 0.01);
                boolean isEntered = false;
                for (Position position : Context.getDataManager().getPositions(deviceId, from, to)) {
                    if (geofence.getGeometry().containsPoint(position.getLatitude(), position.getLongitude())) {
                        if (!isEntered) {
                            isEntered = true;
                            firstPosition = position;
                            geofenceReport = new GeofenceReport();
                            geofenceReport.setGeofenceName(geofence.getName());
                            Device device = Context.getDeviceManager().getById(deviceId);
                            geofenceReport.setGroupName(Context.getGroupsManager().getById(device.getGroupId())
                                .getName());
                            geofenceReport.setDeviceName(device.getName());
                            geofenceReport.setEntryDate(position.getFixTime());
                            ret.add(geofenceReport);
                        }
                    } else {
                        if (isEntered) {
                            isEntered = false;
                            geofenceReport.setExitDate(position.getFixTime());
                            geofenceReport.setDuration(geofenceReport.getExitDate().getTime()
                            - geofenceReport.getEntryDate().getTime());
                            geofenceReport.setSpentFuel(ReportUtils.calculateSpentFuel(firstPosition, position)
                            .doubleValue());
                            geofenceReport.setDistance(ReportUtils.calculateDistance(firstPosition, position,
                            !ignoreOdometer));
                            Collection<StopReport> stopReports = ReportUtils.detectTripsAndStops(
                                    Context.getDataManager().getPositions(deviceId, geofenceReport.getEntryDate(),
                                    geofenceReport.getExitDate()),
                                    Context.getTripsConfig(), ignoreOdometer, speedThreshold, StopReport.class);
                            long stopDuration = 0;
                            for (StopReport stopReport : stopReports) {
                                stopDuration += stopReport.getDuration();
                            }
                            geofenceReport.setStopDuration(stopDuration);
                            Collection<TripReport> tripReports = ReportUtils.detectTripsAndStops(
                                    Context.getDataManager().getPositions(deviceId, geofenceReport.getEntryDate(),
                                    geofenceReport.getExitDate()),
                                    Context.getTripsConfig(), ignoreOdometer, speedThreshold, TripReport.class);
                            long tripDuration = 0;
                            for (TripReport tripReport : tripReports) {
                                tripDuration += tripReport.getDuration();
                            }
                            geofenceReport.setMotionDuration(tripDuration);
                        }
                    }
                }
            }
        }
        return ret;
    }

    public static void getExcel(OutputStream outputStream,
            long userId, Collection<Long> deviceIds, Collection<Long> groupIds,
            Collection<Long> geofenceIds, Date from, Date to) throws SQLException, IOException {
        Collection<GeofenceReport> geofences = getObjects(userId, deviceIds, groupIds, geofenceIds, from, to);
        String templatePath = Context.getConfig().getString("report.templatesPath",
                "templates/export/");
        try (InputStream inputStream = new FileInputStream(templatePath + "/geofences.xlsx")) {
            org.jxls.common.Context jxlsContext = ReportUtils.initializeContext(userId);
            jxlsContext.putVar("geofences", geofences);
            jxlsContext.putVar("from", from);
            jxlsContext.putVar("to", to);
            //added 
            JxlsHelper.getInstance().setUseFastFormulaProcessor(false).processTemplate(inputStream, outputStream, jxlsContext);
        }
    }
}
