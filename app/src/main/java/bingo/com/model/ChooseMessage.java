package bingo.com.model;

public class ChooseMessage {

    String name;

    String phone;

    String time;

    String rawContent;

    String type;

    boolean showingTimeout;

    public ChooseMessage(String name, String phone, String time, String rawContent, String type) {
        this.name = name;
        this.phone = phone;
        this.time = time;
        this.rawContent = rawContent;
        this.type = type;
    }

    public String getPhone() {
        return phone;
    }

    public String getTime() {
        return time;
    }

    public String getRawContent() {
        return rawContent;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public boolean isShowingTimeout() {
        return showingTimeout;
    }

    public void setShowingTimeout(boolean showingTimeout) {
        this.showingTimeout = showingTimeout;
    }
}
