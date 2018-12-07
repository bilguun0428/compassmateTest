package mn.compassmate.reports;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
//import java.math.BigDecimal;
//import java.math.RoundingMode;
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
//import mn.compassmate.helper.DistanceCalculator;
import mn.compassmate.model.Position;
import mn.compassmate.reports.model.SummaryReport;

public final class Summary {

    private static final Logger LOGGER = Logger.getLogger(Summary.class.getName());

    private Summary() {
    }

    private static SummaryReport calculateSummaryResult(long deviceId, Date from, Date to) throws SQLException {
        SummaryReport result = new SummaryReport();
        result.setDeviceId(deviceId);
        result.setDeviceName(Context.getIdentityManager().getById(deviceId).getName());
        Collection<Position> positions = Context.getDataManager().getPositions(deviceId, from, to);
        if (positions != null && !positions.isEmpty()) {
            Position firstPosition = null;
            Position previousPosition = null;
            Position motionPositon = null;
            //Position movePosition = null;
            double speedSum = 0;
            long timeStopSum =0;
            int rescount = 0;
            long moveTimesum = 0;
            long movePosition = 0;
            long timeSum = 0;
            long ignitionOn = 0;
            long motionTime;
            for (Position position : positions) {
                if (firstPosition == null) {
                    firstPosition = position;
                }
               
                previousPosition = position;
                speedSum += position.getSpeed();
                result.setMaxSpeed(position.getSpeed());
                boolean achsan = false;
                //////////////////////////////res count////////////////////////////////// 
                
                //add geofence enter and exit 
                
                if(Position.ALARM_DOOR_PRESSED.equals(position.getAttributes().get(Position.KEY_ALARM))){
                	achsan = true; 
                }
                if(achsan = true && Position.ALARM_DOOR_UNPRESSED.equals(position.getAttributes().get(Position.KEY_ALARM)) ) {
                	achsan = false;
                	rescount++;
                	System.out.println("counted");
                }
                result.setRescount(rescount);
                
                ////////////////////////Distance of speed over 50kphs////////////////////////
                
                double overspeed = position.getSpeed(); 
                //speed on knot 27 is 50kph
                if(overspeed >= 27 ) {
                	boolean ignoreOdometer = Context.getDeviceManager()
                            .lookupAttributeBoolean(deviceId, "report.ignoreOdometer", false, true);                	                	
                    double x = ReportUtils.calculateDistance(firstPosition, previousPosition, !ignoreOdometer);
                    double sum = 0;
                    sum+=x;
                    result.setOverSpeedDistance(sum);
                }
                ///////////////////////////////motion //////////////////////
                boolean movePos = false; 
                if(position.getSpeed() > 0 && !movePos) {
                	movePos= true;
                	movePosition = position.getFixTime().getTime();
                }
                if(position.getSpeed() == 0) {
                	double time = position.getFixTime().getTime() - movePosition;
                	System.out.println(time + "zoruu tsag "); 
                	movePos = false; 
                	timeSum += time;
                	System.out.println(timeSum + "Summarry");   
                	result.setActiveMoveTime(timeSum);
                }

                ////////////////////////////////////stand by /////////////////////////////////////////
                
                boolean ignition = false;
                long ignitionOff=0;
                if(position.getSpeed() == 0 && Position.ALARM_IGNITION_ON.equals(position.getAttributes().get(Position.KEY_ALARM))) {
                    ignitionOn = position.getFixTime().getTime();
                }
                if(Position.ALARM_IGNITION_OFF.equals(position.getAttributes().get(Position.KEY_ALARM))) {
                	
                	if(ignitionOff == 0) {
                		//result.addActiveMoveTime(position.getFixTime().getTime() - from.getTime());
                	}
                	
                	ignition = false;
                	ignitionOff = position.getFixTime().getTime();
                	
                	//System.out.println(ignitionOff - ignitionOn);
                	//result.addActiveMoveTime(ignitionOff - ignitionOn);
                }
            }
                
                
                
                
               
                
            //fuel sensor 
            /*
            double spentFuel = 0.0;
            double first = 0.0;
            double last = 0.0;
            double fuelConsumptionRate = Context.getDeviceManager()
                    .lookupAttributeDouble(deviceId, Fuel.ATTRIBUTE_FUEL_CONSUMPTION_RATE, 100.0, false);
            fuelConsumptionRate /= 1000.0;
            fuelConsumptionRate = BigDecimal.valueOf(fuelConsumptionRate)
                    .setScale(2, RoundingMode.HALF_EVEN).doubleValue();
            double fuelChargeThreshold = Context.getDeviceManager()
                    .lookupAttributeDouble(deviceId, Fuel.ATTRIBUTE_FUEL_CHARGE, 10.0, false);
            Position prevPosition = new Position();
            prevPosition.set(Position.KEY_FUEL_LEVEL, 1010.0);
            positions = Context.getDataManager().getPositions(deviceId, from, to);
            positions.add(prevPosition);
            prevPosition = null;
            boolean isPowerCut = false;
            for (Position position : positions) {
                if (position.getDouble(Position.KEY_FUEL_LEVEL) > 1000.0) {
                    if (first > last) {
                        spentFuel += (first - last);
                    }
                    break;
                }
                if (position.getValid()
                        && position.getAttributes().get(Position.KEY_FUEL_LEVEL) != null
                        && position.getSpeed() > 0) {

                    if (Position.ALARM_POWER_CUT.equals(position.getAttributes().get(Position.KEY_ALARM))) {
                        isPowerCut = true;
                    }
                    if (Position.ALARM_POWER_RESTORED.equals(position.getAttributes().get(Position.KEY_ALARM))) {
                        isPowerCut = false;
                        continue;
                    }
                    if (isPowerCut) {
                        continue;
                    }

                    if (prevPosition == null) {
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

                            first = position.getDouble(Position.KEY_FUEL_LEVEL);
                            last = position.getDouble(Position.KEY_FUEL_LEVEL);

                            prevPosition = new Position();
                            prevPosition.set(Position.KEY_FUEL_LEVEL, position.getDouble(Position.KEY_FUEL_LEVEL));
                            position.set(Position.KEY_FUEL_LEVEL, 1000.0);
                        } else {
                            prevPosition = position;
                            last = prevPosition.getDouble(Position.KEY_FUEL_LEVEL);
                        }

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
                    }
                }
            } //end of fuel 
            spentFuel = BigDecimal.valueOf(spentFuel).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
				*/
            boolean ignoreOdometer = Context.getDeviceManager()
                    .lookupAttributeBoolean(deviceId, "report.ignoreOdometer", false, true);
            result.setDistance(ReportUtils.calculateDistance(firstPosition, previousPosition, !ignoreOdometer));
            result.setAverageSpeed(speedSum / positions.size());
           // result.setSpentFuel(spentFuel);
        }
        return result;
        
    }

    public static Collection<SummaryReport> getObjects(long userId, Collection<Long> deviceIds,
            Collection<Long> groupIds, Date from, Date to) throws InterruptedException {
        ReportUtils.checkPeriodLimit(from, to);
        ArrayList<SummaryReport> result = new ArrayList<>();
        Collection<Long> deviceList = ReportUtils.getDeviceList(deviceIds, groupIds);
        ExecutorService executor = Executors.newFixedThreadPool(deviceList.size());
        List<Callable<SummaryReport>> callableTasks = new ArrayList<>();
        for (long deviceId: deviceList) {
            Callable<SummaryReport> callableTask = () -> {
                 Context.getPermissionsManager().checkDevice(userId, deviceId);
                 return calculateSummaryResult(deviceId, from, to);
            };
            callableTasks.add(callableTask);
        }
        try {
            List<Future<SummaryReport>> futures = executor.invokeAll(callableTasks);
            for (Future<SummaryReport> future : futures) {
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

    public static void getExcel(OutputStream outputStream,
            long userId, Collection<Long> deviceIds, Collection<Long> groupIds,
            Date from, Date to) throws IOException, InterruptedException {
        ReportUtils.checkPeriodLimit(from, to);
        Collection<SummaryReport> summaries = getObjects(userId, deviceIds, groupIds, from, to);
        String templatePath = Context.getConfig().getString("report.templatesPath",
                "templates/export/");
        try (InputStream inputStream = new FileInputStream(templatePath + "/summary.xlsx")) {
            org.jxls.common.Context jxlsContext = ReportUtils.initializeContext(userId);
            jxlsContext.putVar("summaries", summaries);
            jxlsContext.putVar("from", from);
            jxlsContext.putVar("to", to);
            JxlsHelper.getInstance().setUseFastFormulaProcessor(false)
                    .processTemplate(inputStream, outputStream, jxlsContext);
        }
    }
}
