package mn.compassmate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ning.http.client.AsyncHttpClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import org.apache.velocity.app.VelocityEngine;
import org.eclipse.jetty.util.URIUtil;
import mn.compassmate.database.AliasesManager;
import mn.compassmate.database.CalendarManager;
import mn.compassmate.database.AttributesManager;
import mn.compassmate.database.BaseObjectManager;
import mn.compassmate.database.ConnectionManager;
import mn.compassmate.database.DataManager;
import mn.compassmate.database.DeviceManager;
import mn.compassmate.database.DriversManager;
import mn.compassmate.database.IdentityManager;
import mn.compassmate.database.MediaManager;
import mn.compassmate.database.NotificationManager;
import mn.compassmate.database.PermissionsManager;
import mn.compassmate.database.GeofenceManager;
import mn.compassmate.database.GroupsManager;
import mn.compassmate.database.StatisticsManager;
import mn.compassmate.database.UsersManager;
import mn.compassmate.events.MotionEventHandler;
import mn.compassmate.events.OverspeedEventHandler;
import mn.compassmate.geocoder.BingMapsGeocoder;
import mn.compassmate.geocoder.FactualGeocoder;
import mn.compassmate.geocoder.GeocodeFarmGeocoder;
import mn.compassmate.geocoder.GisgraphyGeocoder;
import mn.compassmate.geocoder.GoogleGeocoder;
import mn.compassmate.geocoder.MapQuestGeocoder;
import mn.compassmate.geocoder.NominatimGeocoder;
import mn.compassmate.geocoder.OpenCageGeocoder;
import mn.compassmate.geocoder.Geocoder;
import mn.compassmate.geolocation.UnwiredGeolocationProvider;
import mn.compassmate.helper.Log;
import mn.compassmate.model.Attribute;
import mn.compassmate.model.BaseModel;
import mn.compassmate.model.Calendar;
import mn.compassmate.model.Device;
import mn.compassmate.model.Driver;
import mn.compassmate.model.Geofence;
import mn.compassmate.model.Group;
import mn.compassmate.model.User;
import mn.compassmate.geolocation.GoogleGeolocationProvider;
import mn.compassmate.geolocation.GeolocationProvider;
import mn.compassmate.geolocation.MozillaGeolocationProvider;
import mn.compassmate.geolocation.OpenCellIdGeolocationProvider;
import mn.compassmate.notification.EventForwarder;
import mn.compassmate.reports.model.TripsConfig;
import mn.compassmate.smpp.SmppClient;
import mn.compassmate.web.WebServer;

public final class Context {

    private Context() {
    }

    private static Config config;

    public static Config getConfig() {
        return config;
    }

    private static boolean loggerEnabled;

    public static boolean isLoggerEnabled() {
        return loggerEnabled;
    }

    private static ObjectMapper objectMapper;

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    private static IdentityManager identityManager;

    public static IdentityManager getIdentityManager() {
        return identityManager;
    }

    private static DataManager dataManager;

    public static DataManager getDataManager() {
        return dataManager;
    }

    private static MediaManager mediaManager;

    public static MediaManager getMediaManager() {
        return mediaManager;
    }

    private static UsersManager usersManager;

    public static UsersManager getUsersManager() {
        return usersManager;
    }

    private static GroupsManager groupsManager;

    public static GroupsManager getGroupsManager() {
        return groupsManager;
    }

    private static DeviceManager deviceManager;

    public static DeviceManager getDeviceManager() {
        return deviceManager;
    }

    private static ConnectionManager connectionManager;

    public static ConnectionManager getConnectionManager() {
        return connectionManager;
    }

    private static PermissionsManager permissionsManager;

    public static PermissionsManager getPermissionsManager() {
        return permissionsManager;
    }

    private static Geocoder geocoder;

    public static Geocoder getGeocoder() {
        return geocoder;
    }

    private static GeolocationProvider geolocationProvider;

    public static GeolocationProvider getGeolocationProvider() {
        return geolocationProvider;
    }

    private static WebServer webServer;

    public static WebServer getWebServer() {
        return webServer;
    }

    private static ServerManager serverManager;

    public static ServerManager getServerManager() {
        return serverManager;
    }

    private static GeofenceManager geofenceManager;

    public static GeofenceManager getGeofenceManager() {
        return geofenceManager;
    }

    private static CalendarManager calendarManager;

    public static CalendarManager getCalendarManager() {
        return calendarManager;
    }

    private static NotificationManager notificationManager;

    public static NotificationManager getNotificationManager() {
        return notificationManager;
    }

    private static VelocityEngine velocityEngine;

    public static VelocityEngine getVelocityEngine() {
        return velocityEngine;
    }

    private static final AsyncHttpClient ASYNC_HTTP_CLIENT = new AsyncHttpClient();

    public static AsyncHttpClient getAsyncHttpClient() {
        return ASYNC_HTTP_CLIENT;
    }

    private static EventForwarder eventForwarder;

    public static EventForwarder getEventForwarder() {
        return eventForwarder;
    }

    private static AliasesManager aliasesManager;

    public static AliasesManager getAliasesManager() {
        return aliasesManager;
    }

    private static AttributesManager attributesManager;

    public static AttributesManager getAttributesManager() {
        return attributesManager;
    }

    private static DriversManager driversManager;

    public static DriversManager getDriversManager() {
        return driversManager;
    }

    private static StatisticsManager statisticsManager;

    public static StatisticsManager getStatisticsManager() {
        return statisticsManager;
    }

    private static SmppClient smppClient;

    public static SmppClient getSmppManager() {
        return smppClient;
    }

    private static MotionEventHandler motionEventHandler;

    public static MotionEventHandler getMotionEventHandler() {
        return motionEventHandler;
    }

    private static OverspeedEventHandler overspeedEventHandler;

    public static OverspeedEventHandler getOverspeedEventHandler() {
        return overspeedEventHandler;
    }

    private static TripsConfig tripsConfig;

    public static TripsConfig getTripsConfig() {
        return tripsConfig;
    }

    public static TripsConfig initTripsConfig() {
        return new TripsConfig(
                config.getLong("report.trip.minimalTripDistance", 500),
                config.getLong("report.trip.minimalTripDuration", 300) * 1000,
                config.getLong("report.trip.minimalParkingDuration", 300) * 1000,
                config.getLong("report.trip.minimalNoDataDuration", 3600) * 1000,
                config.getBoolean("report.trip.useIgnition"));
    }

    public static void init(String[] arguments) throws Exception {

        config = new Config();
        if (arguments.length <= 0) {
            throw new RuntimeException("Configuration file is not provided");
        }

        config.load(arguments[0]);

        loggerEnabled = config.getBoolean("logger.enable");
        if (loggerEnabled) {
            Log.setupLogger(config);
        }

        objectMapper = new ObjectMapper();
        objectMapper.setConfig(
                objectMapper.getSerializationConfig().without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS));

        if (config.hasKey("database.url")) {
            dataManager = new DataManager(config);
        }

        if (config.hasKey("media.path")) {
            mediaManager = new MediaManager(config);
        }

        if (dataManager != null) {
            usersManager = new UsersManager(dataManager);
            groupsManager = new GroupsManager(dataManager);
            deviceManager = new DeviceManager(dataManager);
        }

        identityManager = deviceManager;

        if (config.getBoolean("geocoder.enable")) {
            String type = config.getString("geocoder.type", "google");
            String url = config.getString("geocoder.url");
            String key = config.getString("geocoder.key");
            String language = config.getString("geocoder.language");

            int cacheSize = config.getInteger("geocoder.cacheSize");
            switch (type) {
                case "nominatim":
                    geocoder = new NominatimGeocoder(url, key, language, cacheSize);
                    break;
                case "gisgraphy":
                    geocoder = new GisgraphyGeocoder(url, cacheSize);
                    break;
                case "mapquest":
                    geocoder = new MapQuestGeocoder(url, key, cacheSize);
                    break;
                case "opencage":
                    geocoder = new OpenCageGeocoder(url, key, cacheSize);
                    break;
                case "bingmaps":
                    geocoder = new BingMapsGeocoder(url, key, cacheSize);
                    break;
                case "factual":
                    geocoder = new FactualGeocoder(url, key, cacheSize);
                    break;
                case "geocodefarm":
                    geocoder = new GeocodeFarmGeocoder(key, language, cacheSize);
                    break;
                default:
                    geocoder = new GoogleGeocoder(key, language, cacheSize);
                    break;
            }
        }

        if (config.getBoolean("geolocation.enable")) {
            String type = config.getString("geolocation.type", "mozilla");
            String url = config.getString("geolocation.url");
            String key = config.getString("geolocation.key");

            switch (type) {
                case "google":
                    geolocationProvider = new GoogleGeolocationProvider(key);
                    break;
                case "opencellid":
                    geolocationProvider = new OpenCellIdGeolocationProvider(key);
                    break;
                case "unwired":
                    geolocationProvider = new UnwiredGeolocationProvider(url, key);
                    break;
                default:
                    geolocationProvider = new MozillaGeolocationProvider(key);
                    break;
            }
        }

        if (config.getBoolean("web.enable")) {
            webServer = new WebServer(config, dataManager.getDataSource());
        }

        permissionsManager = new PermissionsManager(dataManager, usersManager);

        connectionManager = new ConnectionManager();

        tripsConfig = initTripsConfig();

        if (config.getBoolean("event.enable")) {
            geofenceManager = new GeofenceManager(dataManager);
            calendarManager = new CalendarManager(dataManager);
            notificationManager = new NotificationManager(dataManager);
            Properties velocityProperties = new Properties();
            velocityProperties.setProperty("file.resource.loader.path",
                    Context.getConfig().getString("templates.rootPath", "templates") + "/");
            velocityProperties.setProperty("runtime.log.logsystem.class",
                    "org.apache.velocity.runtime.log.NullLogChute");

            String address;
            try {
                address = config.getString("web.address", InetAddress.getLocalHost().getHostAddress());
            } catch (UnknownHostException e) {
                address = "localhost";
            }

            String webUrl = URIUtil.newURI("http", address, config.getInteger("web.port", 8082), "", "");
            webUrl = Context.getConfig().getString("web.url", webUrl);
            velocityProperties.setProperty("web.url", webUrl);

            velocityEngine = new VelocityEngine();
            velocityEngine.init(velocityProperties);

            motionEventHandler = new MotionEventHandler(tripsConfig);
            overspeedEventHandler = new OverspeedEventHandler(
                    Context.getConfig().getLong("event.overspeed.minimalDuration") * 1000,
                    Context.getConfig().getBoolean("event.overspeed.notRepeat"));
        }

        serverManager = new ServerManager();

        if (config.getBoolean("event.forward.enable")) {
            eventForwarder = new EventForwarder();
        }

        aliasesManager = new AliasesManager(dataManager);

        attributesManager = new AttributesManager(dataManager);

        driversManager = new DriversManager(dataManager);

        statisticsManager = new StatisticsManager();

        if (config.getBoolean("sms.smpp.enable")) {
            smppClient = new SmppClient();
        }

    }

    public static void init(IdentityManager testIdentityManager) {
        config = new Config();
        objectMapper = new ObjectMapper();
        identityManager = testIdentityManager;
    }

    public static <T extends BaseModel> BaseObjectManager<T> getManager(Class<T> clazz) {
        if (clazz.equals(Device.class)) {
            return (BaseObjectManager<T>) deviceManager;
        } else if (clazz.equals(Group.class)) {
            return (BaseObjectManager<T>) groupsManager;
        } else if (clazz.equals(User.class)) {
            return (BaseObjectManager<T>) usersManager;
        } else if (clazz.equals(Calendar.class)) {
            return (BaseObjectManager<T>) calendarManager;
        } else if (clazz.equals(Attribute.class)) {
            return (BaseObjectManager<T>) attributesManager;
        } else if (clazz.equals(Geofence.class)) {
            return (BaseObjectManager<T>) geofenceManager;
        } else if (clazz.equals(Driver.class)) {
            return (BaseObjectManager<T>) driversManager;
        }
        return null;
    }

}
