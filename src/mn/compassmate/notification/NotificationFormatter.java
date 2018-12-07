package mn.compassmate.notification;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.tools.generic.DateTool;
import org.apache.velocity.tools.generic.NumberTool;

import mn.compassmate.Context;
import mn.compassmate.helper.Log;
import mn.compassmate.model.Device;
import mn.compassmate.model.Event;
import mn.compassmate.model.Position;
import mn.compassmate.model.User;
import mn.compassmate.reports.ReportUtils;

public final class NotificationFormatter {

    private NotificationFormatter() {
    }

    public static VelocityContext prepareContext(long userId, Event event, Position position) {
        User user = Context.getPermissionsManager().getUser(userId);
        Device device = Context.getIdentityManager().getById(event.getDeviceId());

        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("user", user);
        velocityContext.put("device", device);
        velocityContext.put("event", event);
        if (position != null) {
            velocityContext.put("position", position);
            velocityContext.put("speedUnits", ReportUtils.getSpeedUnit(userId));
        }
        if (event.getGeofenceId() != 0) {
            velocityContext.put("geofence", Context.getGeofenceManager().getById(event.getGeofenceId()));
        }
        String driverUniqueId = event.getString(Position.KEY_DRIVER_UNIQUE_ID);
        if (driverUniqueId != null) {
            velocityContext.put("driver", Context.getDriversManager().getDriverByUniqueId(driverUniqueId));
        }
        velocityContext.put("webUrl", Context.getVelocityEngine().getProperty("web.url"));
        velocityContext.put("dateTool", new DateTool());
        velocityContext.put("numberTool", new NumberTool());
        velocityContext.put("timezone", ReportUtils.getTimezone(userId));
        velocityContext.put("locale", Locale.getDefault());
        return velocityContext;
    }

    public static VelocityContext prepareContext(long userId, Map<String, Object> mapData) {
        User user = Context.getPermissionsManager().getUser(userId);

        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("user", user);

        for (Entry<String, Object> entry : mapData.entrySet()) {
            velocityContext.put(entry.getKey(), entry.getValue());
        }

        velocityContext.put("webUrl", Context.getVelocityEngine().getProperty("web.url"));
        velocityContext.put("dateTool", new DateTool());
        velocityContext.put("numberTool", new NumberTool());
        velocityContext.put("timezone", ReportUtils.getTimezone(userId));
        velocityContext.put("locale", Locale.getDefault());
        return velocityContext;
    }

    public static Template getTemplate(Event event, String path) {
        Template template;
        try {
            template = Context.getVelocityEngine().getTemplate(path + event.getType() + ".vm",
                    StandardCharsets.UTF_8.name());
        } catch (ResourceNotFoundException error) {
            Log.warning(error);
            template = Context.getVelocityEngine().getTemplate(path + "unknown.vm",
                    StandardCharsets.UTF_8.name());
        }
        return template;
    }

    public static Template getTemplate(String path) {
        Template template;
        try {
            template = Context.getVelocityEngine().getTemplate(path + ".vm",
                    StandardCharsets.UTF_8.name());
        } catch (ResourceNotFoundException error) {
            Log.warning(error);
            template = Context.getVelocityEngine().getTemplate(path + "unknown.vm",
                    StandardCharsets.UTF_8.name());
        }
        return template;
    }

    public static MailMessage formatMailMessage(long userId, Event event, Position position) {
        VelocityContext velocityContext = prepareContext(userId, event, position);
        StringWriter writer = new StringWriter();
        getTemplate(event, Context.getConfig().getString("mail.templatesPath", "mail") + "/")
                .merge(velocityContext, writer);
        return new MailMessage((String) velocityContext.get("subject"), writer.toString());
    }

    public static MailMessage formatMailMessage(long userId, String templateName, Map<String, Object> mapData) {
        VelocityContext velocityContext = prepareContext(userId, mapData);
        StringWriter writer = new StringWriter();
        getTemplate(Context.getConfig().getString("mail.templatesPath", "mail") + "/" + templateName)
                .merge(velocityContext, writer);
        return new MailMessage((String) velocityContext.get("subject"), writer.toString());
    }

    public static String formatSmsMessage(long userId, Event event, Position position) {
        VelocityContext velocityContext = prepareContext(userId, event, position);
        StringWriter writer = new StringWriter();
        getTemplate(event, Context.getConfig().getString("sms.templatesPath", "sms") + "/")
                .merge(velocityContext, writer);
        return writer.toString();
    }
}
