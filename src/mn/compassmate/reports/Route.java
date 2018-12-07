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
import mn.compassmate.model.Position;
import mn.compassmate.reports.model.DeviceReport;

public final class Route {

    private static final Logger LOGGER = Logger.getLogger(Route.class.getName());

    private Route() {
    }

    public static Collection<Position> getObjects(long userId, Collection<Long> deviceIds, Collection<Long> groupIds,
            Date from, Date to) throws InterruptedException {
        ReportUtils.checkPeriodLimit(from, to);
        ArrayList<Position> result = new ArrayList<>();
        Collection<Long> deviceList = ReportUtils.getDeviceList(deviceIds, groupIds);
        ExecutorService executor = Executors.newFixedThreadPool(deviceList.size());
        List<Callable<Collection<Position>>> callableTasks = new ArrayList<>();
        for (long deviceId: deviceList) {
            Callable<Collection<Position>> callableTask = () -> {
                Context.getPermissionsManager().checkDevice(userId, deviceId);
                List<Position> positions = new ArrayList<>();
                Collection<Position> positionList = Context.getDataManager().getPositions(deviceId, from, to);
                boolean isPowerCut = false;
                boolean isPowerRestore = false;
                Position prevPosition = null;
                double prevFuel = -1d;
                for (Position position : positionList) {
                    if (Position.ALARM_POWER_CUT.equals(position.getAttributes().get(Position.KEY_ALARM))) {
                        isPowerCut = true;
                        positions.remove(positions.size() - 1);
                        prevFuel = prevPosition.getDouble(Position.KEY_FUEL_LEVEL);
                    }
                    if (Position.ALARM_POWER_RESTORED.equals(position.getAttributes().get(Position.KEY_ALARM))) {
                        if (!isPowerCut) {
                            positions.clear();
                        }
                        isPowerCut = false;
                        isPowerRestore = true;
                        continue;
                    }
                    if (!isPowerCut) {
                        if (!isPowerRestore) {
                            positions.add(position);
                        } else {
                            if (prevFuel == 0) {
                                positions.add(position);
                            } else if (Math.abs(prevFuel - position.getDouble(Position.KEY_FUEL_LEVEL)) < 2) {
                                positions.add(position);
                            }
                            isPowerRestore = false;
                        }
                        prevPosition = position;
                    }
                }
                return positions;
            };
            callableTasks.add(callableTask);
        }
        try {
            List<Future<Collection<Position>>> futures = executor.invokeAll(callableTasks);
            for (Future<Collection<Position>> future : futures) {
              result.addAll(future.get());
            }
            LOGGER.log(Level.FINE, "attempt to shutdown executor");
            executor.shutdown();
            executor.awaitTermination(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            throw e;
        } catch (ExecutionException e) {
            LOGGER.log(Level.FINE, e.getMessage());
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
    public static Collection<Position> getObjects(long userId, Collection<Long> deviceIds, Collection<Long> groupIds,
            Date from, Date to) throws SQLException {
        ReportUtils.checkPeriodLimit(from, to);
        ArrayList<Position> result = new ArrayList<>();
        double spentFuel = 0.0;
        double chargedFuel = 0.0;
        for (long deviceId: ReportUtils.getDeviceList(deviceIds, groupIds)) {
            double fuelConsumptionRate = Context.getDeviceManager()
                    .lookupAttributeDouble(deviceId, Fuel.ATTRIBUTE_FUEL_CONSUMPTION_RATE, 100.0, false);
            fuelConsumptionRate /= 1000.0;
            fuelConsumptionRate = BigDecimal.valueOf(fuelConsumptionRate)
                    .setScale(2, RoundingMode.HALF_EVEN).doubleValue();
            double fuelChargeThreshold = Context.getDeviceManager()
                    .lookupAttributeDouble(deviceId, Fuel.ATTRIBUTE_FUEL_CHARGE, 10.0, false);
            Context.getPermissionsManager().checkDevice(userId, deviceId);
            Position prevPosition = new Position();
            prevPosition.set(Position.KEY_FUEL_LEVEL, 1010.0);
            double first = 0.0;
            double last = 0.0;
            Collection<Position> positions = Context.getDataManager().getPositions(deviceId, from, to);
            positions.add(prevPosition);
            prevPosition = null;
            for (Position position : positions) {
                if (position.getDouble(Position.KEY_FUEL_LEVEL) > 1000.0) {
                    if (first > last) {
                        spentFuel += (first - last);
                    }
                    //result.add(position);
                    break;
                }
                if (position.getValid()
                        && position.getAttributes().get(Position.KEY_FUEL_LEVEL) != null
                        && position.getAttributes().get(Position.KEY_ALARM) == null
                        && position.getSpeed() > 0) {
                    if (prevPosition == null) {
                        result.add(position);
                        first = position.getDouble(Position.KEY_FUEL_LEVEL);
                        last = position.getDouble(Position.KEY_FUEL_LEVEL);
                        prevPosition = position;
                        continue;
                    }

                    if (prevPosition.getDouble(Position.KEY_FUEL_LEVEL)
                            < position.getDouble(Position.KEY_FUEL_LEVEL)) { //might be charging

                        double deltaFuel = position.getDouble(Position.KEY_FUEL_LEVEL)
                                - prevPosition.getDouble(Position.KEY_FUEL_LEVEL);
                        if (deltaFuel >= fuelChargeThreshold) { //charging
                            if (first > last) {
                                spentFuel += (first - last);
                            }

                            chargedFuel += (position.getDouble(Position.KEY_FUEL_LEVEL)
                                    - prevPosition.getDouble(Position.KEY_FUEL_LEVEL));

                            first = position.getDouble(Position.KEY_FUEL_LEVEL);
                            last = position.getDouble(Position.KEY_FUEL_LEVEL);

                            prevPosition = new Position();
                            prevPosition.set(Position.KEY_FUEL_LEVEL, position.getDouble(Position.KEY_FUEL_LEVEL));
                            position.set(Position.KEY_FUEL_LEVEL, 1000.0);
                        } else {
                            prevPosition = position;
                            last = prevPosition.getDouble(Position.KEY_FUEL_LEVEL);
                        }

                        result.add(position);
                        continue;
                    }

                    double deltaFuel = prevPosition.getDouble(Position.KEY_FUEL_LEVEL)
                            - position.getDouble(Position.KEY_FUEL_LEVEL);
                    deltaFuel = BigDecimal.valueOf(deltaFuel).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
                    double distance = DistanceCalculator.distance(
                            prevPosition.getLatitude(),
                            prevPosition.getLongitude(),
                            position.getLatitude(),
                            position.getLongitude());
                    distance = BigDecimal.valueOf(distance).setScale(2, RoundingMode.HALF_EVEN).doubleValue();

                    double usedFuelPredicted = distance * fuelConsumptionRate;

                    if (usedFuelPredicted >= deltaFuel) {
                        prevPosition = position;
                        last = prevPosition.getDouble(Position.KEY_FUEL_LEVEL);
                        result.add(position);
                    }
                }
            }
            spentFuel = BigDecimal.valueOf(spentFuel).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
            chargedFuel = BigDecimal.valueOf(chargedFuel).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
            System.out.println(spentFuel);
            System.out.println(chargedFuel);
        }
        return result;
    }
*/
    public static void getExcel(OutputStream outputStream,
            long userId, Collection<Long> deviceIds, Collection<Long> groupIds,
            Date from, Date to) throws SQLException, IOException {
        ReportUtils.checkPeriodLimit(from, to);
        ArrayList<DeviceReport> devicesRoutes = new ArrayList<>();
        ArrayList<String> sheetNames = new ArrayList<>();
        for (long deviceId: ReportUtils.getDeviceList(deviceIds, groupIds)) {
            Context.getPermissionsManager().checkDevice(userId, deviceId);
            Collection<Position> positions = Context.getDataManager()
                    .getPositions(deviceId, from, to);
            DeviceReport deviceRoutes = new DeviceReport();
            Device device = Context.getIdentityManager().getById(deviceId);
            deviceRoutes.setDeviceName(device.getName());
            sheetNames.add(WorkbookUtil.createSafeSheetName(deviceRoutes.getDeviceName()));
            if (device.getGroupId() != 0) {
                Group group = Context.getGroupsManager().getById(device.getGroupId());
                if (group != null) {
                    deviceRoutes.setGroupName(group.getName());
                }
            }
            deviceRoutes.setObjects(positions);
            devicesRoutes.add(deviceRoutes);
        }
        String templatePath = Context.getConfig().getString("report.templatesPath",
                "templates/export/");
        try (InputStream inputStream = new FileInputStream(templatePath + "/route.xlsx")) {
            org.jxls.common.Context jxlsContext = ReportUtils.initializeContext(userId);
            jxlsContext.putVar("devices", devicesRoutes);
            jxlsContext.putVar("sheetNames", sheetNames);
            jxlsContext.putVar("from", from);
            jxlsContext.putVar("to", to);
            ReportUtils.processTemplateWithSheets(inputStream, outputStream, jxlsContext);
        }
    }
}
