package mn.compassmate.notification;

import java.util.Date;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import mn.compassmate.Context;
import mn.compassmate.helper.Log;
import mn.compassmate.model.Event;
import mn.compassmate.model.Position;
import mn.compassmate.model.User;

public final class NotificationMail {

    private NotificationMail() {
    }

    private static Properties getProperties(PropertiesProvider provider) {
        Properties properties = new Properties();
        String host = provider.getString("mail.smtp.host");
        if (host != null) {
            properties.put("mail.transport.protocol", provider.getString("mail.transport.protocol", "smtp"));
            properties.put("mail.smtp.host", host);
            properties.put("mail.smtp.port", provider.getString("mail.smtp.port", "25"));

            String starttlsEnable = provider.getString("mail.smtp.starttls.enable");
            if (starttlsEnable != null) {
                properties.put("mail.smtp.starttls.enable", Boolean.parseBoolean(starttlsEnable));
            }
            String starttlsRequired = provider.getString("mail.smtp.starttls.required");
            if (starttlsRequired != null) {
                properties.put("mail.smtp.starttls.required", Boolean.parseBoolean(starttlsRequired));
            }

            String sslEnable = provider.getString("mail.smtp.ssl.enable");
            if (sslEnable != null) {
                properties.put("mail.smtp.ssl.enable", Boolean.parseBoolean(sslEnable));
            }
            String sslTrust = provider.getString("mail.smtp.ssl.trust");
            if (sslTrust != null) {
                properties.put("mail.smtp.ssl.trust", sslTrust);
            }

            String sslProtocols = provider.getString("mail.smtp.ssl.protocols");
            if (sslProtocols != null) {
                properties.put("mail.smtp.ssl.protocols", sslProtocols);
            }

            String username = provider.getString("mail.smtp.username");
            if (username != null) {
                properties.put("mail.smtp.username", username);
            }
            String password = provider.getString("mail.smtp.password");
            if (password != null) {
                properties.put("mail.smtp.password", password);
            }
            String from = provider.getString("mail.smtp.from");
            if (from != null) {
                properties.put("mail.smtp.from", from);
            }
        }
        return properties;
    }

    public static void sendMailSync(long userId, Event event, Position position) throws MessagingException {
        User user = Context.getPermissionsManager().getUser(userId);

        Properties properties = getProperties(new PropertiesProvider(Context.getConfig()));
        if (!properties.containsKey("mail.smtp.host")) {
            properties = getProperties(new PropertiesProvider(user));
            if (!properties.containsKey("mail.smtp.host")) {
                Log.warning("No SMTP configuration found");
                return;
            }
        }

        Session session = Session.getInstance(properties);

        MimeMessage message = new MimeMessage(session);

        String from = properties.getProperty("mail.smtp.from");
        if (from != null) {
            message.setFrom(new InternetAddress(from));
        }

        message.addRecipient(Message.RecipientType.TO, new InternetAddress(user.getEmail()));
        MailMessage mailMessage = NotificationFormatter.formatMailMessage(userId, event, position);
        message.setSubject(mailMessage.getSubject());
        message.setSentDate(new Date());
        message.setContent(mailMessage.getBody(), "text/html; charset=utf-8");

        Transport transport = session.getTransport();
        try {
            Context.getStatisticsManager().registerMail();
            transport.connect(
                    properties.getProperty("mail.smtp.host"),
                    properties.getProperty("mail.smtp.username"),
                    properties.getProperty("mail.smtp.password"));
            transport.sendMessage(message, message.getAllRecipients());
        } finally {
            transport.close();
        }
    }

    public static void sendMailAsync(final long userId, final Event event, final Position position) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    sendMailSync(userId, event, position);
                } catch (MessagingException error) {
                    Log.warning(error);
                }
            }
        }).start();
    }

    public static void sendMailSync(long userId, String templateName, Map<String, Object> mapData)
        throws MessagingException {
        User user = Context.getPermissionsManager().getUser(userId);

        Properties properties = getProperties(new PropertiesProvider(Context.getConfig()));
        if (!properties.containsKey("mail.smtp.host")) {
            properties = getProperties(new PropertiesProvider(user));
            if (!properties.containsKey("mail.smtp.host")) {
                Log.warning("No SMTP configuration found");
                return;
            }
        }

        Session session = Session.getInstance(properties);

        MimeMessage message = new MimeMessage(session);

        String from = properties.getProperty("mail.smtp.from");
        if (from != null) {
            message.setFrom(new InternetAddress(from));
        }

        message.addRecipient(Message.RecipientType.TO, new InternetAddress(user.getEmail()));

        MailMessage mailMessage = NotificationFormatter.formatMailMessage(userId, templateName, mapData);
        message.setSubject(mailMessage.getSubject());
        message.setSentDate(new Date());
        message.setContent(mailMessage.getBody(), "text/html; charset=utf-8");

        Transport transport = session.getTransport();
        try {
            Context.getStatisticsManager().registerMail();
            transport.connect(
                    properties.getProperty("mail.smtp.host"),
                    properties.getProperty("mail.smtp.username"),
                    properties.getProperty("mail.smtp.password"));
            transport.sendMessage(message, message.getAllRecipients());
        } finally {
            transport.close();
        }
    }

}
