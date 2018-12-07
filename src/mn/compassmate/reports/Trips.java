package mn.compassmate.reports;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jxls.util.JxlsHelper;

import mn.compassmate.Context;
import mn.compassmate.helper.Log;
import mn.compassmate.model.Device;
import mn.compassmate.model.Group;
import mn.compassmate.reports.model.DeviceReport;
import mn.compassmate.reports.model.TripReport;

public final class Trips {

    private static final Logger LOGGER = Logger.getLogger(Trips.class.getName());

    private Trips() {
    }

    private static Collection<TripReport> detectTrips(long deviceId, Date from, Date to) throws SQLException {
        double speedThreshold = Context.getConfig().getDouble("event.motion.speedThreshold", 0.01);

        boolean ignoreOdometer = Context.getDeviceManager()
                .lookupAttributeBoolean(deviceId, "report.ignoreOdometer", false, true);

        return ReportUtils.detectTripsAndStops(
                Context.getDataManager().getPositions(deviceId, from, to),
                Context.getTripsConfig(), ignoreOdometer, speedThreshold, TripReport.class);
    }

    public static Collection<TripReport> getObjects(long userId, Collection<Long> deviceIds, Collection<Long> groupIds,
            Date from, Date to) throws InterruptedException {
        ReportUtils.checkPeriodLimit(from, to);
        ArrayList<TripReport> result = new ArrayList<>();
        Collection<Long> deviceList = ReportUtils.getDeviceList(deviceIds, groupIds);
        ExecutorService executor = Executors.newFixedThreadPool(deviceList.size());
        List<Callable<Collection<TripReport>>> callableTasks = new ArrayList<>();
        for (long deviceId: deviceList) {
            Callable<Collection<TripReport>> callableTask = () -> {
                Context.getPermissionsManager().checkDevice(userId, deviceId);
                return detectTrips(deviceId, from, to);
            };
            callableTasks.add(callableTask);
        }
        try {
            List<Future<Collection<TripReport>>> futures = executor.invokeAll(callableTasks);
            for (Future<Collection<TripReport>> future : futures) {
              result.addAll(future.get());
            }
            LOGGER.log(Level.FINE, "attempt to shutdown executor");
            executor.shutdown();
            executor.awaitTermination(20, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            throw e;
        } catch (ExecutionException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        } finally {
            if (!executor.isTerminated()) {
                LOGGER.log(Level.FINE, "cancel non-finished tasks");
            }
            executor.shutdownNow();
            LOGGER.log(Level.FINE, "shutdown finished");
        }
        return result;
    }
/*
    public static void getExcel(OutputStream outputStream,
            long userId, Collection<Long> deviceIds, Collection<Long> groupIds,
            Date from, Date to) throws SQLException, IOException {
        ReportUtils.checkPeriodLimit(from, to);
        ArrayList<DeviceReport> devicesTrips = new ArrayList<>();
        ArrayList<String> sheetNames = new ArrayList<>();
        for (long deviceId: ReportUtils.getDeviceList(deviceIds, groupIds)) {
            Context.getPermissionsManager().checkDevice(userId, deviceId);
            Collection<TripReport> trips = detectTrips(deviceId, from, to);
            DeviceReport deviceTrips = new DeviceReport();
            Device device = Context.getIdentityManager().getById(deviceId);
            deviceTrips.setDeviceName(device.getName());
            sheetNames.add(WorkbookUtil.createSafeSheetName(deviceTrips.getDeviceName()));
            if (device.getGroupId() != 0) {
                Group group = Context.getGroupsManager().getById(device.getGroupId());
                if (group != null) {
                    deviceTrips.setGroupName(group.getName());
                }
            }
            deviceTrips.setObjects(trips);
            devicesTrips.add(deviceTrips);
        }
        String templatePath = Context.getConfig().getString("report.templatesPath",
                "templates/export/");
        try (InputStream inputStream = new FileInputStream(templatePath + "/trips.xlsx")) {
            org.jxls.common.Context jxlsContext = ReportUtils.initializeContext(userId);
            jxlsContext.putVar("devices", devicesTrips);
            jxlsContext.putVar("sheetNames", sheetNames);
            jxlsContext.putVar("from", from);
            jxlsContext.putVar("to", to);
            ReportUtils.processTemplateWithSheets(inputStream, outputStream, jxlsContext);
        }
    }
*/
    public static void getExcel(OutputStream outputStream,
            long userId, Collection<Long> deviceIds, Collection<Long> groupIds,
            Date from, Date to) throws IOException, InterruptedException {
        ReportUtils.checkPeriodLimit(from, to);
        Collection<DeviceReport> devicesTrips = new ArrayList<>();
        Collection<Long> deviceList = ReportUtils.getDeviceList(deviceIds, groupIds);
        ExecutorService executor = Executors.newFixedThreadPool(deviceList.size());
        List<Callable<DeviceReport>> callableTasks = new ArrayList<>();
        for (long deviceId: deviceList) {
            Callable<DeviceReport> callableTask = () -> {
                Context.getPermissionsManager().checkDevice(userId, deviceId);
                Collection<TripReport> trips = detectTrips(deviceId, from, to);
                DeviceReport deviceTrips = new DeviceReport();
                Device device = Context.getIdentityManager().getById(deviceId);
                deviceTrips.setDeviceName(device.getName());
                if (device.getGroupId() != 0) {
                    Group group = Context.getGroupsManager().getById(device.getGroupId());
                    if (group != null) {
                        deviceTrips.setGroupName(group.getName());
                    }
                }
                deviceTrips.setObjects(trips);
                return deviceTrips;
            };
            callableTasks.add(callableTask);
        }
        try {
            List<Future<DeviceReport>> futures = executor.invokeAll(callableTasks);
            for (Future<DeviceReport> future : futures) {
                devicesTrips.add(future.get());
            }
            LOGGER.log(Level.FINE, "attempt to shutdown executor");
            executor.shutdown();
            executor.awaitTermination(20, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            throw e;
        } catch (ExecutionException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        } finally {
            if (!executor.isTerminated()) {
                LOGGER.log(Level.FINE, "cancel non-finished tasks");
            }
            executor.shutdownNow();
            LOGGER.log(Level.FINE, "shutdown finished");
        }
        String templatePath = Context.getConfig().getString("report.templatesPath",
                "templates/export/");
        try {
            InputStream inputStream = new FileInputStream(templatePath + "/trips.xlsx");
            org.jxls.common.Context jxlsContext = ReportUtils.initializeContext(userId);
            jxlsContext.putVar("devices", devicesTrips);
            jxlsContext.putVar("from", from);
            jxlsContext.putVar("to", to);
            JxlsHelper.getInstance().setUseFastFormulaProcessor(false)
                    .processTemplate(inputStream, outputStream, jxlsContext);
        } catch (FileNotFoundException err) {
            Log.error(Log.exceptionStack(err));
        }
    }
}
