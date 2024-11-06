package do_an.tkll.an_iot_app;

public class Task {
    private String condition;
    private String comparison;
    private String value;
    private String action;
    private String device;

    public Task(String condition, String comparison, String value, String action, String device) {
        this.condition = condition;
        this.comparison = comparison;
        this.value = value;
        this.action = action;
        this.device = device;
    }

    public String getCondition() { return condition; }
    public String getComparison() { return comparison; }
    public String getValue() { return value; }
    public String getAction() { return action; }
    public String getDevice() { return device; }

    public String getDescription() {
        return "Khi " + condition + " " + comparison + " " + value + " => " + action + " " + device;
    }
}
