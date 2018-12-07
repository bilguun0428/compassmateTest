package mn.compassmate.model;

public class Notification extends ExtendedModel {

    private long userId;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private boolean web;

    public boolean getWeb() {
        return web;
    }

    public void setWeb(boolean web) {
        this.web = web;
    }

    private boolean mail;

    public boolean getMail() {
        return mail;
    }

    public void setMail(boolean mail) {
        this.mail = mail;
    }

    private boolean sms;

    public boolean getSms() {
        return sms;
    }

    public void setSms(boolean sms) {
        this.sms = sms;
    }
}
