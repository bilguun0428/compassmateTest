package mn.compassmate.reports;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
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

import mn.compassmate.Context;
import mn.compassmate.model.Position;
import mn.compassmate.reports.model.FuelReport;

import org.jxls.util.JxlsHelper;

/**
 *
 * @author baasanbatpurevjal
 */
public final class Fuel {

    private static final Logger LOGGER = Logger.getLogger(Fuel.class.getName());

    private Fuel() {
    }

    public static final String ATTRIBUTE_FUEL_FROP_NORMAL = "fuelDropNormal";
    public static final String ATTRIBUTE_FUEL_CHARGE = "fuelChargeThreshold";
    public static final String ATTRIBUTE_FUEL_CONSUMPTION_RATE = "fuelConsumptionRate";

/*
    private static FuelReport calculateFuelResult(long deviceId, Date from, Date to) throws SQLException {
        FuelReport result = new FuelReport();
        double firstValue = 0.0;
        double secondValue = 0.0;
        double spentFuel = 0.0;
        int counter = 0;
        boolean first = true;
        double chargedFuel = 0;
        result.setDeviceId(deviceId);
        result.setDeviceName(Context.getIdentityManager().getById(deviceId).getName());
        Collection<Position> positions = Context.getDataManager().getPositions(deviceId, from, to);
        if (positions != null && !positions.isEmpty()) {
            for (Position position : positions) {
                if (++counter > 97) {
                    if (!first) {
                        if (firstValue > secondValue) {
                            spentFuel += (firstValue - secondValue) / 97.0;
                        } else {
                            chargedFuel += (secondValue - firstValue) / 97.0;
                        }
                        firstValue = secondValue;
                        secondValue = 0.0;
                    } else {
                        firstValue = secondValue;
                        secondValue = 0.0;
                        first = false;
                    }
                    counter = 1;
                    secondValue += position.getAttributes().get(Position.KEY_FUEL_LEVEL)
                            != null ? position.getDouble(Position.KEY_FUEL_LEVEL)
                            : 0.0;
                } else {
                    secondValue += position.getAttributes().get(Position.KEY_FUEL_LEVEL)
                            != null ? position.getDouble(Position.KEY_FUEL_LEVEL)
                            : 0.0;
                }
            }
        }
        result.setSpentFuel(spentFuel);
        result.setChargedFuel(chargedFuel);
        return result;
    }
*/
/*
    private static FuelReport calculateFuelResult(long deviceId, Date from, Date to) throws SQLException {
        FuelReport result = new FuelReport();
        double mmax = 0.0, mmin = 0.0;
        double spentFuel = 0.0;
        //double firstTotalDistance = 0.0;
        //double lastTotalDistance = 0.0;
        boolean first = true;
        double chargedFuel = 0;
        double normalDropInterval = Context.getDeviceManager()
                .lookupAttributeDouble(deviceId, ATTRIBUTE_FUEL_FROP_NORMAL, 2.0, false);
        double chargedFuelThreshold = Context.getDeviceManager()
                .lookupAttributeDouble(deviceId, FuelChargeEventHandler.ATTRIBUTE_FUEL_CHARGE_THRESHOLD, 9.0, false);
        result.setDeviceId(deviceId);
        result.setDeviceName(Context.getIdentityManager().getById(deviceId).getName());
        Collection<Position> positions = Context.getDataManager().getPositions(deviceId, from, to);
        if (positions != null && !positions.isEmpty()) {
            for (Position position : positions) {
                if (position.getAttributes().get(Position.KEY_FUEL_LEVEL) != null) {
                    double current = position.getDouble(Position.KEY_FUEL_LEVEL);
                    if (first) {
                        //firstTotalDistance = position.getDouble(Position.KEY_TOTAL_DISTANCE);
                        mmax = current;
                        mmin = current;
                        first = false;
                    } else {
                        if (current < mmin) {
                            double diff = (mmin - current);
                            if (diff <= normalDropInterval) {
                                mmin = current;
                            }
                        } else {
                            double diff = (current - mmin);
                            if (diff >= chargedFuelThreshold) {
                                chargedFuel += diff;
                                spentFuel += (mmax - mmin);
                                mmax = current;
                                mmin = current;
                            }
                        }
                    }
                }
                //lastTotalDistance = position.getDouble(Position.KEY_TOTAL_DISTANCE);
            }
        }
        spentFuel += (mmax - mmin);
        //double fuelConsumptionRate = Context.getDeviceManager()
                //.lookupAttributeDouble(deviceId, ATTRIBUTE_FUEL_CONSUMPTION_RATE, 100.0, false);
        //fuelConsumptionRate /= 100000;
        //double byRate = fuelConsumptionRate * (lastTotalDistance - firstTotalDistance);
        //spentFuel = BigDecimal.valueOf((byRate + spentFuel) / 2.0).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
        result.setSpentFuel(spentFuel);
        result.setChargedFuel(chargedFuel);
        return result;
    }
*/
// Versoin 3
/*
    private static FuelReport calculateFuelResult(long deviceId, Date from, Date to) throws SQLException {
        FuelReport result = new FuelReport();
        double spentFuel = 0.0;
        double chargedFuel = 0;
        double chargedFuelThreshold = Context.getDeviceManager()
                .lookupAttributeDouble(deviceId, FuelChargeEventHandler.ATTRIBUTE_FUEL_CHARGE_THRESHOLD, 9.0, false);
        result.setDeviceId(deviceId);
        result.setDeviceName(Context.getIdentityManager().getById(deviceId).getName());
        Collection<Position> positions = Context.getDataManager().getPositions(deviceId, from, to);
        Position prev;
        Position current = null;
        ArrayList al = new ArrayList<>();
        for (Position position : positions) {
            if (position.getAttributes().containsKey(Position.KEY_FUEL_LEVEL)) {
                if (current == null) {
                    current = position;
                    continue;
                }
                prev = current;
                current = position;

                double currentFuel = current.getDouble(Position.KEY_FUEL_LEVEL);
                double prevFuel = prev.getDouble(Position.KEY_FUEL_LEVEL);
                if ((currentFuel - prevFuel) >= chargedFuelThreshold) {
                    if (!al.isEmpty()) {
                        ImmutablePair<BigDecimal, BigDecimal> equation = ReportUtils.predictEquation(al);
                        spentFuel +=
                                (ReportUtils.solveEquation(equation, 0)
                                - ReportUtils.solveEquation(equation, al.size() - 1));
                        al.clear();
                        continue;
                    }
                }

                al.add(prev.getDouble(Position.KEY_FUEL_LEVEL));
            }
        }
        if (!al.isEmpty()) {
            ImmutablePair<BigDecimal, BigDecimal> equation = ReportUtils.predictEquation(al);
            spentFuel +=
                    (ReportUtils.solveEquation(equation, 0)
                    - ReportUtils.solveEquation(equation, al.size() - 1));
            al.clear();
        }
        result.setSpentFuel(spentFuel);
        result.setChargedFuel(chargedFuel);
        return result;
    }
*/

// version 4
/*
    private static FuelReport calculateFuelResult(long deviceId, Date from, Date to) throws SQLException {
        FuelReport result = new FuelReport();
        result.setDeviceName(Context.getIdentityManager().getById(deviceId).getName());
        result.setDeviceId(deviceId);
        double spentFuel = 0.0;
        double chargedFuel = 0.0;
        double normalDropInterval = Context.getDeviceManager()
            .lookupAttributeDouble(deviceId, ATTRIBUTE_FUEL_FROP_NORMAL, 15.0, false);
        double chargedFuelThreshold = Context.getDeviceManager()
            .lookupAttributeDouble(deviceId, FuelChargeEventHandler.ATTRIBUTE_FUEL_CHARGE_THRESHOLD, 9.0, false);
        Collection<Position> positions = Context.getDataManager().getPositions(deviceId, from, to);
        Collection<Position> ret = new ArrayList<>();
        Position preP = null;
        if (positions != null && !positions.isEmpty()) {
            for (Position position : positions) {
                if (position.getSpeed() > 0.5
                        && position.getAttributes().get(Position.KEY_FUEL_LEVEL) != null) {
                    ret.add(position);
                }
                if (preP == null) {
                    preP = position;
                    continue;
                }
                if (preP.getAttributes().get(Position.KEY_FUEL_LEVEL) != null
                        && position.getAttributes().get(Position.KEY_FUEL_LEVEL) != null) {
                    if (position.getDouble(Position.KEY_FUEL_LEVEL)
                            > preP.getDouble(Position.KEY_FUEL_LEVEL)) {
                        double diff = position.getDouble(Position.KEY_FUEL_LEVEL)
                                - preP.getDouble(Position.KEY_FUEL_LEVEL);
                        if (diff >= chargedFuelThreshold) {
                            chargedFuel += diff;
                        }
                    }
                }
                preP = position;
            }
        }
        positions = new ArrayList<>();
        Position prevPosition = null;
        if (!ret.isEmpty()) {
            for (Position position : ret) {
                if (prevPosition == null) {
                    prevPosition = position;
                    positions.add(position);
                    continue;
                }
                double diff = prevPosition.getDouble(Position.KEY_FUEL_LEVEL)
                        - position.getDouble(Position.KEY_FUEL_LEVEL);
                if (diff <= normalDropInterval) {
                    positions.add(position);
                    prevPosition = position;
                }
            }
        }
        double max = -1;
        double min = -1;
        prevPosition = null;
        for (Position position : positions) {
            if (prevPosition == null) {
                max = position.getDouble(Position.KEY_FUEL_LEVEL);
                min = max;
                prevPosition = position;
                continue;
            }
            double current = position.getDouble(Position.KEY_FUEL_LEVEL);
            if (current < min) {
                min = current;
                prevPosition = position;
                continue;
            }
            if ((current - prevPosition.getDouble(Position.KEY_FUEL_LEVEL))
                    >= chargedFuelThreshold) {
                spentFuel += (max - min);
                prevPosition = position;
                max = current;
                min = current;
                continue;
            }
            prevPosition = position;
        }
        spentFuel += (max - min);
        result.setSpentFuel(spentFuel);
        result.setChargedFuel(chargedFuel);
        return result;
    }
*/
    /*
    private static FuelReport calculateFuelResult(long deviceId, Date from, Date to) throws SQLException {
        FuelReport result = new FuelReport();
        result.setDeviceName(Context.getIdentityManager().getById(deviceId).getName());
        result.setDeviceId(deviceId);
        double spentFuel = 0.0;
        double chargedFuel = 0.0;
        Collection<Position> positions = Context.getDataManager().getPositions(deviceId, from, to);
        if (positions != null && !positions.isEmpty()) {
            ArrayList<Double> al = new ArrayList<>();
            for (Position position : positions) {
                if (position.getAttributes().get(Position.KEY_FUEL_LEVEL) != null) {
                    //System.out.println(position.getDouble(Position.KEY_FUEL_LEVEL));
                    al.add(position.getDouble(Position.KEY_FUEL_LEVEL));
                }
            }
            double[] y = new double[al.size()];
            double[] x = new double[al.size()];
            for (int i = 0; i < al.size(); i++) {
                y[i] = al.get(i);
                x[i] = i;
            }
            double bandwidth = Context.getDeviceManager()
            .lookupAttributeDouble(deviceId, "bandwidth", 0.25, false);
            LoessInterpolator li = new LoessInterpolator(bandwidth, 2);
            double[] ret = li.smooth(x, y);
            for (int i = 1; i < ret.length; i++) {
                System.out.println(ret[i]);
                double prev = ret[i - 1];
                double current = ret[i];
                if (prev > current) { //fuel consuming
                    spentFuel += (prev - current);
                } else { //charging
                    chargedFuel += (current - prev);
                }
            }
        }
        result.setSpentFuel(spentFuel);
        result.setChargedFuel(chargedFuel);
        return result;
    }

    public static Collection<FuelReport> getObjects(long userId, Collection<Long> deviceIds, Collection<Long> groupIds,
            Date from, Date to) throws SQLException {
        ReportUtils.checkPeriodLimit(from, to);
        ArrayList<FuelReport> result = new ArrayList<>();
        for (long deviceId: ReportUtils.getDeviceList(deviceIds, groupIds)) {
            Context.getPermissionsManager().checkDevice(userId, deviceId);
            result.add(calculateFuelResult(deviceId, from, to));
        }
        return result;
    }
*/
//version 5
/*
    private static Collection<TripReport> detectTrips(long deviceId, Date from, Date to) throws SQLException {
        double speedThreshold = Context.getConfig().getDouble("event.motion.speedThreshold", 0.01);

        boolean ignoreOdometer = Context.getDeviceManager()
                .lookupAttributeBoolean(deviceId, "report.ignoreOdometer", false, true);

        Collection<TripReport> result = ReportUtils.detectTripsAndStops(
                Context.getDataManager().getPositions(deviceId, from, to),
                Context.getTripsConfig(), ignoreOdometer, speedThreshold, TripReport.class);

        return result;
    }

    private static FuelReport calculateFuelResult(long deviceId, Date from, Date to) throws SQLException {
        FuelReport result = new FuelReport();
        result.setDeviceName(Context.getIdentityManager().getById(deviceId).getName());
        result.setDeviceId(deviceId);
        double spentFuel = 0.0;
        double chargedFuel = 0.0;
        Collection<TripReport> trips = detectTrips(deviceId, from, to);
        for (TripReport tripReport : trips) {
            Collection<Position> positions = Context.getDataManager()
                    .getPositions(tripReport.getDeviceId(), tripReport.getStartTime(), tripReport.getEndTime());
            Position prevPosition = null;
            double max = 0.0;
            double min = 0.0;
            for (Position position : positions) {
                if (position.getAttributes().get(Position.KEY_FUEL_LEVEL) != null) {
                    double current = position.getDouble(Position.KEY_FUEL_LEVEL);
                    if (prevPosition == null) {
                        prevPosition = position;
                        max = current;
                        min = current;
                        continue;
                    }

                    double prev = prevPosition.getDouble(Position.KEY_FUEL_LEVEL);
                    double diff = current - prev;
                    if (diff >= 5.0) {
                        spentFuel += (max - min);
                        max = current;
                        min = current;
                        prevPosition = position;
                        continue;
                    }

                    min = (min > current ? current : min);
                    prevPosition = position;
                }
            }
            spentFuel += (max > min ? (max - min) : 0.0);
        }
        result.setSpentFuel(spentFuel);
        result.setChargedFuel(chargedFuel);
        return result;
    }
*/
//version 6
    private static FuelReport calculateFuelResult(long deviceId, Date from, Date to) throws SQLException {
        FuelReport result = new FuelReport();
        result.setDeviceName(Context.getIdentityManager().getById(deviceId).getName());
        result.setDeviceId(deviceId);

        double firstFuelLevel = 0;
        double lastFuelLevel = 0.0;
        double spentFuel = 0.0;
        double chargedFuel = 0.0;
        double chargedFuelSum = 0.0;
        long chargedCount = 0;
        
        Collection<Position> positions = Context.getDataManager().getPositions(deviceId, from, to);
        /*
        mn.compassmate.model.Geofence geofence = Context.getDataManager()
                .getObject(mn.compassmate.model.Geofence.class, geofenceId );
        */
         
        Position prevPosition = null;
        Position firstPosition = null;
        boolean isFuelCharging = false;
        boolean isEntered = false;

        boolean isPowerCut = false;
        boolean isPowerRestore = false;
        for (Position position : positions) {
            if (position.getAttributes().get(Position.KEY_FUEL_LEVEL) != null) {
                if (Position.ALARM_POWER_CUT.equals(position.getAttributes().get(Position.KEY_ALARM))) {
                    isPowerCut = true;
                }
             
                
                if (Position.ALARM_POWER_RESTORED.equals(position.getAttributes().get(Position.KEY_ALARM))) {
                    if (!isPowerCut) {
                        chargedFuel = 0;
                    }
                    isPowerCut = false;
                    isPowerRestore = true;
                    continue;
                }
                if (isPowerCut) {
                    continue;
                }
                if (prevPosition == null) {
                    prevPosition = position;
                    double fuel = position.getDouble(Position.KEY_FUEL_LEVEL);
                    if (fuel > 0) {
                        firstFuelLevel = position.getDouble(Position.KEY_FUEL_LEVEL);
                    }
                    continue;
                }
                if (isPowerRestore) {
                    if (firstFuelLevel == 0) {
                        firstFuelLevel = position.getDouble(Position.KEY_FUEL_LEVEL);
                    }
                    isPowerRestore = false;             
                }
               //author Bilguun 
                //version 1.3 
                /*
                double current = position.getDouble(Position.KEY_FUEL_LEVEL);
                if(firstPosition == null) {
                	firstPosition = position;                	
                }
                double diff = firstPosition.getDouble(Position.KEY_FUEL_LEVEL) - current;
                if(diff > 10.0) {
                	if(Position.ALARM_FUEL_ADD.equals(position.getAttributes().get(Position.KEY_ALARM))){
                		System.out.println("tsengelj baina");
                	}
                }
                */
                
                
                //version 1.2 
                
                if(Position.ALARM_GEOFENCE_ENTER.equals(position.getAttributes().get(Position.KEY_ALARM))) {
                	isEntered = true;
                	firstPosition = position;
                	System.out.println("isEntered " + position.getFixTime());
                }
                
                if(isEntered && Position.ALARM_FUEL_ADD.equals(position.getAttributes().get(Position.KEY_ALARM))){
                		isFuelCharging = true;
                		System.out.println("isCharging" + position.getFixTime());
                }
               
                if(isFuelCharging && Position.ALARM_GEOFENCE_EXIT.equals(position.getAttributes().get(Position.KEY_ALARM))) {
                	isEntered = false;
                	isFuelCharging = false; 
                	chargedFuel = position.getDouble(Position.KEY_FUEL_LEVEL) - firstPosition.getDouble(Position.KEY_FUEL_LEVEL);
                	chargedFuelSum += chargedFuel;
                	chargedCount++;
                	System.out.println("IsExit");
                }
                
                
                
             /// version 1.1
             /*
                if(geofence.getGeometry().containsPoint(position.getLatitude(), position.getLongitude())) {
                	if(!isEntered) {
                		System.out.println("Enetered");
                		isEntered = true;
                		firstPosition = position;
                		
                	}
                	if(Position.ALARM_FUEL_ADD.equals(position.getAttributes().get(Position.KEY_ALARM))){
            			isFuelCharging = true;
            		}
                }else {
                	if(isEntered && isFuelCharging) {
                		System.out.println("working");
                        isEntered = false;
                        isFuelCharging = false;
                        System.out.println("firstPostion time" + firstPosition.getFixTime());
                        System.out.println("currentPosition time " + position.getFixTime());
                    	chargedFuel = position.getDouble(Position.KEY_FUEL_LEVEL) - firstPosition.getDouble(Position.KEY_FUEL_LEVEL);
                    	chargedFuelSum += chargedFuel; 
                    	chargedCount++;
                	}
                }    
                */
                prevPosition = position;
                lastFuelLevel = position.getDouble(Position.KEY_FUEL_LEVEL);
            }
        }
        spentFuel = firstFuelLevel - lastFuelLevel  + chargedFuelSum;
        //chargedFuel = BigDecimal.valueOf(chargedFuel).setScale(2, RoundingMode.HALF_EVEN).doubleValue();

        result.setSpentFuel(spentFuel);
        result.setChargedFuel(chargedFuelSum);
        result.setChargedCount(chargedCount);
        result.setFirstFuelVelocity(firstFuelLevel);
        result.setLastFuelVelocity(lastFuelLevel);
        return result;
    }

    public static Collection<FuelReport> getObjects(long userId, Collection<Long> deviceIds, Collection<Long> groupIds,
            Date from, Date to) throws InterruptedException {
        ReportUtils.checkPeriodLimit(from, to);
        ArrayList<FuelReport> result = new ArrayList<>();

        Collection<Long> deviceList = ReportUtils.getDeviceList(deviceIds, groupIds);
        ExecutorService executor = Executors.newFixedThreadPool(deviceList.size());
        List<Callable<FuelReport>> callableTasks = new ArrayList<>();
        
          for (long deviceId: deviceList) {
            Callable<FuelReport> callableTask = () -> {
                Context.getPermissionsManager().checkDevice(userId, deviceId);
                return calculateFuelResult(deviceId, from, to);
           };
           callableTasks.add(callableTask);
        	}
        
        try {
            List<Future<FuelReport>> futures = executor.invokeAll(callableTasks);
            for (Future<FuelReport> future : futures) {
              result.add(future.get());
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
        Collection<FuelReport> fuelReports = getObjects(userId, deviceIds, groupIds, to, from);
        String templatePath = Context.getConfig().getString("report.templatesPath",
                "templates/export/");
        try (InputStream inputStream = new FileInputStream(templatePath + "/fuel.xlsx")) {
            org.jxls.common.Context jxlsContext = ReportUtils.initializeContext(userId);
            jxlsContext.putVar("summaries", fuelReports);
            jxlsContext.putVar("from", from);
            jxlsContext.putVar("to", to);
            JxlsHelper.getInstance().setUseFastFormulaProcessor(false)
                    .processTemplate(inputStream, outputStream, jxlsContext);
        }
    }
}
