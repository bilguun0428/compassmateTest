package mn.compassmate.database;

import com.ning.http.client.Request;
import com.ning.http.client.RequestBuilder;
import org.joda.time.format.ISODateTimeFormat;
import mn.compassmate.Context;
import mn.compassmate.helper.Log;
import mn.compassmate.model.Statistics;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class StatisticsManager {

    private static final int SPLIT_MODE = Calendar.DAY_OF_MONTH;

    private int lastUpdate = Calendar.getInstance().get(SPLIT_MODE);

    private Set<Long> users = new HashSet<>();
    private Set<Long> devices = new HashSet<>();

    private int requests;
    private int messagesReceived;
    private int messagesStored;
    private int mailSent;
    private int smsSent;
    private int geocoderRequests;
    private int geolocationRequests;

    private void checkSplit() {
        int currentUpdate = Calendar.getInstance().get(SPLIT_MODE);
        if (lastUpdate != currentUpdate) {
            Statistics statistics = new Statistics();
            statistics.setCaptureTime(new Date());
            statistics.setActiveUsers(users.size());
            statistics.setActiveDevices(devices.size());
            statistics.setRequests(requests);
            statistics.setMessagesReceived(messagesReceived);
            statistics.setMessagesStored(messagesStored);
            statistics.setMailSent(mailSent);
            statistics.setSmsSent(smsSent);
            statistics.setGeocoderRequests(geocoderRequests);
            statistics.setGeolocationRequests(geolocationRequests);

            try {
                Context.getDataManager().addObject(statistics);
            } catch (SQLException e) {
                Log.warning(e);
            }

            String url = Context.getConfig().getString("server.statistics");
            if (url != null) {
                String time = ISODateTimeFormat.dateTime().print(statistics.getCaptureTime().getTime());
                Request request = new RequestBuilder("POST")
                        .setUrl(url)
                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .addFormParam("version", Log.getAppVersion())
                        .addFormParam("captureTime", time)
                        .addFormParam("activeUsers", String.valueOf(statistics.getActiveUsers()))
                        .addFormParam("activeDevices", String.valueOf(statistics.getActiveDevices()))
                        .addFormParam("requests", String.valueOf(statistics.getRequests()))
                        .addFormParam("messagesReceived", String.valueOf(statistics.getMessagesReceived()))
                        .addFormParam("messagesStored", String.valueOf(statistics.getMessagesStored()))
                        .addFormParam("mailSent", String.valueOf(statistics.getMailSent()))
                        .addFormParam("smsSent", String.valueOf(statistics.getSmsSent()))
                        .addFormParam("geocoderRequests", String.valueOf(statistics.getGeocoderRequests()))
                        .addFormParam("geolocationRequests", String.valueOf(statistics.getGeolocationRequests()))
                        .build();
                Context.getAsyncHttpClient().prepareRequest(request).execute();
            }

            users.clear();
            devices.clear();
            requests = 0;
            messagesReceived = 0;
            messagesStored = 0;
            mailSent = 0;
            smsSent = 0;
            geocoderRequests = 0;
            geolocationRequests = 0;
            lastUpdate = currentUpdate;
        }
    }

    public synchronized void registerRequest(long userId) {
        checkSplit();
        requests += 1;
        if (userId != 0) {
            users.add(userId);
        }
    }

    public synchronized void registerMessageReceived() {
        checkSplit();
        messagesReceived += 1;
    }

    public synchronized void registerMessageStored(long deviceId) {
        checkSplit();
        messagesStored += 1;
        if (deviceId != 0) {
            devices.add(deviceId);
        }
    }

    public synchronized void registerMail() {
        checkSplit();
        mailSent += 1;
    }

    public synchronized void registerSms() {
        checkSplit();
        smsSent += 1;
    }

    public synchronized void registerGeocoderRequest() {
        checkSplit();
        geocoderRequests += 1;
    }

    public synchronized void registerGeolocationRequest() {
        checkSplit();
        geolocationRequests += 1;
    }

}
