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
import mn.compassmate.helper.DateUtil;
import mn.compassmate.model.Device;
import mn.compassmate.model.Group;
import mn.compassmate.model.Position;
import mn.compassmate.reports.model.DailyReport;
import mn.compassmate.reports.model.DailyReportDetail;
import mn.compassmate.reports.model.StopReport;

import org.jxls.util.JxlsHelper;

/**
 *
 * @author baasanbatpurevjal
 */
public final class Daily {

    private static final Logger LOGGER = Logger.getLogger(Daily.class.getName());

    private Daily() {
    }

    private static DailyReport calculateDailyReport(long deviceId, Date from, Date to) throws SQLException {
        DailyReport dailyReport = new DailyReport();
        dailyReport.setDeviceId(deviceId);
        dailyReport.setDeviceName(Context.getIdentityManager().getById(deviceId).getName());
       
        Collection<Position> positions = Context.getDataManager().getPositions(deviceId, from, to);
        if(positions != null && !positions.isEmpty()) {
        	Position firstPosition = null;
        	Position prevPostiion = null;
        	
        	Position entryPosition = null;
        	Position exitPosition = null;

    		boolean loaded = false; 
    		long entryDate;
    		long endDate; 
    		int count = 0;
    		
        	for(Position position: positions) {
        		if(firstPosition == null) {
        			firstPosition = position; 
        		}
        		
        	if(Position.ALARM_DOOR_PRESSED.equals(position.getAttributes().get(Position.KEY_ALARM))) {
        		loaded = true;
        		entryPosition = position;
        		entryDate = position.getFixTime().getTime();
        		//dailyReport.setEndTime(entryDate);
        		
        		}
        	if(loaded && Position.ALARM_DOOR_UNPRESSED.equals(position.getAttributes().get(Position.KEY_ALARM))) {
        		loaded = false; 
        		endDate = position.getFixTime().getTime();
        		count++;
        		}
        	}
        }
        return dailyReport;
    }

    public static Collection<DailyReport> getObjects(long userId, Collection<Long> deviceIds, Collection<Long> groupIds,
            Date from, Date to) throws InterruptedException {
        ReportUtils.checkPeriodLimit(from, to);
        ArrayList<DailyReport> result = new ArrayList<>();
        Collection<Long> deviceList = ReportUtils.getDeviceList(deviceIds, groupIds);
        ExecutorService executor = Executors.newFixedThreadPool(deviceList.size());
        List<Callable<DailyReport>> callableTasks = new ArrayList<>();
        for (long deviceId : deviceList) {
            Callable<DailyReport> callableTask = () -> {
                Context.getPermissionsManager().checkDevice(userId, deviceId);
                return calculateDailyReport(deviceId, from, to);
           };
           callableTasks.add(callableTask);
        }
        try {
            List<Future<DailyReport>> futures = executor.invokeAll(callableTasks);
            for (Future<DailyReport> future : futures) {
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
        Collection<DailyReport> dailyReports =
                getObjects(userId, deviceIds, groupIds, from, to);
        String templatePath = Context.getConfig().getString("report.templatesPath",
                "templates/export/");
        try (InputStream inputStream = new FileInputStream(templatePath + "/daily_report.xlsx")) {
            org.jxls.common.Context jxlsContext = ReportUtils.initializeContext(userId);
            String groupName = "";
            Group group = null;

            if (deviceIds.isEmpty()) {
                group = Context.getGroupsManager().getById(groupIds.iterator().next());
            } else {
                Device device = Context.getIdentityManager().getById(deviceIds.iterator().next());
                if (device.getGroupId() != 0) {
                    group = Context.getGroupsManager().getById(device.getGroupId());
                }
            }

            if (group != null) {
                groupName = group.getName();
            }

            jxlsContext.putVar("reports", dailyReports);
            jxlsContext.putVar("from", from);
            jxlsContext.putVar("to", to);
            jxlsContext.putVar("groupName", groupName);
            JxlsHelper.getInstance().setUseFastFormulaProcessor(false)
                    .processTemplate(inputStream, outputStream, jxlsContext);
        }
    }
}
