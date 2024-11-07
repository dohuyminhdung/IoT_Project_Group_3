package do_an.tkll.an_iot_app;

public class Scheduler {
    private String deviceName;
    private String time;
    private String description;
    private String onOff;

    public Scheduler(String deviceName, String time, String description, String onOff) {
        this.deviceName = deviceName;
        this.time = time;
        this.description = description;
        this.onOff = onOff;
    }
    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getOnOff() { return onOff; }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
