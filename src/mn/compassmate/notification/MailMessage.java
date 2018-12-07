package mn.compassmate.notification;

public class MailMessage {

    private String subject;
    private String body;

    public MailMessage(String subject, String body) {
        this.subject = subject;
        this.body = body;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }
}
