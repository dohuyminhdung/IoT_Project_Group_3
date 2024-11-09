package do_an.tkll.an_iot_app;

public class ConditionRule {
    public String sensorType; // Ví dụ: "temperature" hoặc "humidity"
    public String device;     // Thiết bị cần bật/tắt
    public String operation;  // "on" hoặc "off"
    public float threshold;   // Ngưỡng giá trị để kích hoạt quy tắc
    public String comparisonType; // true nếu quy tắc là "> threshold", false nếu là "< threshold"

    public ConditionRule(String sensorType, String device, String operation, float threshold, String comparisonType) {
        this.sensorType = sensorType;
        this.device = device;
        this.operation = operation;
        this.threshold = threshold;
        this.comparisonType = comparisonType;
    }
}
