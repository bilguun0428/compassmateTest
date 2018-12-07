package mn.compassmate.reports;

import java.io.FileInputStream;
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

import org.apache.poi.ss.util.WorkbookUtil;
import mn.compassmate.Context;
import mn.compassmate.model.Device;
import mn.compassmate.model.Group;
import mn.compassmate.reports.model.DeviceReport;
import mn.compassmate.reports.model.StopReport;

public final class Stops {

    private static final Logger LOGGER = Logger.getLogger(Stops.class.getName());

    private Stops() {
    }

    private static Collection<StopReport> detectStops(long deviceId, Date from, Date to) throws SQLException {
        double speedThreshold = Context.getConfig().getDouble("event.motion.speedThreshold", 0.01);

        boolean ignoreOdometer = Context.getDeviceManager()
                .lookupAttributeBoolean(deviceId, "report.ignoreOdometer", false, true);

        return ReportUtils.detectTripsAndStops(
                Context.getDataManager().getPositions(deviceId, from, to),
                Context.getTripsConfig(), ignoreOdometer, speedThreshold, StopReport.class);
    }

    public static Collection<StopReport> getObjects(
            long userId, Collection<Long> deviceIds, Collection<Long> groupIds,
            Date from, Date to) throws InterruptedException {
        ReportUtils.checkPeriodLimit(from, to);
        ArrayList<StopReport> result = new ArrayList<>();
        Collection<Long> deviceList = ReportUtils.getDeviceList(deviceIds, groupIds);
        ExecutorService executor = Executors.newFixedThreadPool(deviceList.size());
        List<Callable<Collection<StopReport>>> callableTasks = new ArrayList<>();
        for (long deviceId: deviceList) {
            Callable<Collection<StopReport>> callableTask = () -> {
                Context.getPermissionsManager().checkDevice(userId, deviceId);
                return detectStops(deviceId, from, to);
           };
           callableTasks.add(callableTask);
        }
        try {
            List<Future<Collection<StopReport>>> futures = executor.invokeAll(callableTasks);
            for (Future<Collection<StopReport>> future : futures) {
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

    public static void getExcel(
            OutputStream outputStream, long userId, Collection<Long> deviceIds, Collection<Long> groupIds,
            Date from, Date to) throws IOException, InterruptedException {
        ReportUtils.checkPeriodLimit(from, to);
        ArrayList<DeviceReport> devicesStops = new ArrayList<>();
        ArrayList<String> sheetNames = new ArrayList<>();

        Collection<Long> deviceList = ReportUtils.getDeviceList(deviceIds, groupIds);
        ExecutorService executor = Executors.newFixedThreadPool(deviceList.size());
        List<Callable<DeviceReport>> callableTasks = new ArrayList<>();

        for (long deviceId: ReportUtils.getDeviceList(deviceIds, groupIds)) {
            Callable<DeviceReport> callableTask = () -> {
                Context.getPermissionsManager().checkDevice(userId, deviceId);
                Collection<StopReport> stops = detectStops(deviceId, from, to);
                DeviceReport deviceStops = new DeviceReport();
                Device device = Context.getIdentityManager().getById(deviceId);
                deviceStops.setDeviceName(device.getName());
                sheetNames.add(WorkbookUtil.createSafeSheetName(deviceStops.getDeviceName()));
                if (device.getGroupId() != 0) {
                    Group group = Context.getGroupsManager().getById(device.getGroupId());
                    if (group != null) {
                        deviceStops.setGroupName(group.getName());
                    }
                }
                deviceStops.setObjects(stops);
                return deviceStops;
            };
            callableTasks.add(callableTask);
        }
        try {
            List<Future<DeviceReport>> futures = executor.invokeAll(callableTasks);
            for (Future<DeviceReport> future : futures) {
                devicesStops.add(future.get());
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
        try (InputStream inputStream = new FileInputStream(templatePath + "/stops.xlsx")) {
            org.jxls.common.Context jxlsContext = ReportUtils.initializeContext(userId);
            jxlsContext.putVar("devices", devicesStops);
            jxlsContext.putVar("sheetNames", sheetNames);
            jxlsContext.putVar("from", from);
            jxlsContext.putVar("to", to);
            ReportUtils.processTemplateWithSheets(inputStream, outputStream, jxlsContext);
        }
    }

}
