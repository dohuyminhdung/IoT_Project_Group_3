#define CONFIG_THINGSBOARD_ENABLE_DEBUG false

#include <global.h>

#define CURRENT_FIRMWARE_TITLE "OTA_BTL"
#define CURRENT_FIRMWARE_VERSION "1.0.1"

const uint32_t FIRE_CONFIRM_TIME_MS = 3000;
  const uint32_t FIRE_CLEAR_TIME_MS = 3000; 

constexpr int16_t TELEMETRY_SEND_INTERVAL = 5000U;
constexpr uint8_t FIRMWARE_FAILURE_RETRIES = 12U;
constexpr uint16_t FIRMWARE_PACKET_SIZE = 4096U;

constexpr char WIFI_SSID[] = "your_wifi_ssid_here"; // Replace with your actual WiFi SSID
constexpr char WIFI_PASSWORD[] = "your_wifi_password_here"; // Replace with your actual WiFi password
constexpr char TOKEN[] = "your_token_here"; // Replace with your actual ThingsBoard token
constexpr char THINGSBOARD_SERVER[] = "app.coreiot.io";
constexpr char TEMPERATURE_KEY[] = "temperature";
constexpr char HUMIDITY_KEY[] = "humidity";
constexpr char LIGHT_KEY[] = "light";
constexpr char MQ2_KEY[] = "mq2";
constexpr char FIRE_KEY[] = "fire";

constexpr uint16_t THINGSBOARD_PORT = 1883U;
constexpr uint16_t MAX_MESSAGE_SEND_SIZE = 1024U;
constexpr uint16_t MAX_MESSAGE_RECEIVE_SIZE = 1024U;
constexpr uint32_t SERIAL_DEBUG_BAUD = 115200U;
constexpr uint64_t REQUEST_TIMEOUT_MICROSECONDS = 10000U * 1000U;
constexpr int16_t telemetrySendInterval = 3000U;
constexpr uint16_t BLINKING_INTERVAL_MS_MIN = 10U;
constexpr uint16_t BLINKING_INTERVAL_MS_MAX = 60000U;
volatile uint16_t blinkingInterval = 1000U;
constexpr char BLINKING_INTERVAL_ATTR[] = "blinkingInterval";
constexpr char LED_MODE_ATTR[] = "ledMode";

constexpr char LED_STATE_ATTR[] = "led";
constexpr char FAN_STATE_ATTR[] = "fan";
constexpr char SERVO_STATE_ATTR[] = "servo";

constexpr uint8_t MAX_ATTRIBUTES = 3U;
constexpr std::array<const char *, 3U> SHARED_ATTRIBUTES_LIST = {
  LED_STATE_ATTR,
  FAN_STATE_ATTR,
  SERVO_STATE_ATTR
};

WiFiClient wifi_client;
Arduino_MQTT_Client mqttClient(wifi_client);
Servo myServo;

OTA_Firmware_Update<> ota;
Shared_Attribute_Update<1U, MAX_ATTRIBUTES> shared_update;
Attribute_Request<2U, MAX_ATTRIBUTES> attr_request;
const std::array<IAPI_Implementation*, 3U> apis = { &shared_update, &attr_request, &ota };
ThingsBoard tb(mqttClient, MAX_MESSAGE_RECEIVE_SIZE, MAX_MESSAGE_SEND_SIZE, Default_Max_Stack_Size, apis);
Espressif_Updater<> updater;


// bool shared_update_subscribed = false;
bool subscribedShared = false;
bool currentFWSent = false;
bool updateRequestSent = false;
bool requestedShared = false;

// volatile bool ledState = false;
// volatile bool ledStateChanged = false;
// volatile uint16_t blinkingInterval = 1000U;
// constexpr uint8_t MAX_ATTRIBUTES = 1U;


void taskServo(bool servoState)
{
    if (servoState)
    {
      myServo.write(90);
      vTaskDelay(1000 );
    }
    else
    {
      myServo.write(0);
      vTaskDelay(1000);
    }
}

void processSharedAttributes(const JsonObjectConst &data) {
  Serial.println("Process shared attributes");
  if (data.containsKey(BLINKING_INTERVAL_ATTR)) {
    const uint16_t new_interval = data[BLINKING_INTERVAL_ATTR].as<uint16_t>();
    if (new_interval >= BLINKING_INTERVAL_MS_MIN && new_interval <= BLINKING_INTERVAL_MS_MAX) {
      blinkingInterval = new_interval;
      Serial.print("Blinking interval is set to: ");
      Serial.println(new_interval);
    }
  }
  Serial.println("Shared attributes processed");
  if (data.containsKey(LED_STATE_ATTR)) {
    ledState = data[LED_STATE_ATTR].as<bool>();
    ledStateChanged = 1;
    // Serial.print("LED state is set to: ");
    // Serial.println(ledState);
  }
  if (data.containsKey(FAN_STATE_ATTR)) {
    fanState = data[FAN_STATE_ATTR].as<bool>();
    fanStateChanged = 1;
    // Serial.print("Fan state is set to: ");
    // Serial.println(fanState);
  }
  if (data.containsKey(SERVO_STATE_ATTR)) {
    servoState = data[SERVO_STATE_ATTR].as<bool>();
    servoStateChanged = 1;
    // Serial.print("Servo state is set to: ");
    // Serial.println(servoState);
  }
}


void requestTimedOut() {
  Serial.printf("Attribute request timed out after %llu microseconds.\n", REQUEST_TIMEOUT_MICROSECONDS);
}

void update_starting_callback() {}

void finished_callback(const bool & success) {
  if (success) {
    Serial.println("Done, Reboot now");
    esp_restart();
  } else {
    Serial.println("Downloading firmware failed");
  }
}

void progress_callback(const size_t & current, const size_t & total) {
  Serial.printf("Progress %.2f%%\n", static_cast<float>(current * 100U) / total);
}

// void processSharedAttributeUpdate(const JsonObjectConst &data) {
//   const size_t jsonSize = Helper::Measure_Json(data);
//   char buffer[jsonSize];
//   serializeJson(data, buffer, jsonSize);
//   Serial.println(buffer);
//   Serial.println("Processing shared attribute update");
// }

void processSharedAttributeRequest(const JsonObjectConst &data) {
  const size_t jsonSize = Helper::Measure_Json(data);
  char buffer[jsonSize];
  serializeJson(data, buffer, jsonSize);
  Serial.println(buffer);
  Serial.println("Processing shared attribute request");
}


void taskWifiConnection(void* pvParameters){
    Serial.println("Connecting to AP ...");
    WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
    while (WiFi.status() != WL_CONNECTED) {
      delay(500);
      Serial.print(".");
    }
    Serial.println("Connected to AP");
    while(1){
      // Serial.printf("WiFi status: %d\n", WiFi.status());
      if(WiFi.status() != WL_CONNECTED){
        Serial.println("WiFi disconnected, reconnecting...");
        WiFi.reconnect();
      }
      vTaskDelay(5000);
    }
  }

void taskThingsBoard(void* pvParameters){
    while (1){
      if (!tb.connected()){
        Serial.println("Connecting to ThingsBoard...");
        if (!tb.connect(THINGSBOARD_SERVER, TOKEN, THINGSBOARD_PORT)){
          Serial.println("Failed to connect");
          vTaskDelay(5000);
          continue;
        }else{
          Serial.println("Connected to ThingsBoard");
          // Share_attribute 

          if (!requestedShared) {
            const Attribute_Request_Callback<MAX_ATTRIBUTES> sharedCallback(&processSharedAttributeRequest, REQUEST_TIMEOUT_MICROSECONDS, &requestTimedOut, SHARED_ATTRIBUTES_LIST);
            requestedShared = attr_request.Shared_Attributes_Request(sharedCallback);
          }
          if (!subscribedShared) {
            const Shared_Attribute_Callback<MAX_ATTRIBUTES> callback(&processSharedAttributes, SHARED_ATTRIBUTES_LIST);
            subscribedShared = shared_update.Shared_Attributes_Subscribe(callback);
          }
        }
      }
      if (!currentFWSent) {
        currentFWSent = ota.Firmware_Send_Info(CURRENT_FIRMWARE_TITLE, CURRENT_FIRMWARE_VERSION);
      }
  
      if (!updateRequestSent) {
        const OTA_Update_Callback callback(
          CURRENT_FIRMWARE_TITLE, CURRENT_FIRMWARE_VERSION,
          &updater,
          &finished_callback,
          &progress_callback,
          &update_starting_callback,
          FIRMWARE_FAILURE_RETRIES,
          FIRMWARE_PACKET_SIZE
        );
  
        bool started = ota.Start_Firmware_Update(callback);
        bool subscribed = ota.Subscribe_Firmware_Update(callback);
      
        if (started && subscribed) {
          Serial.println("Firmware Update Started & Subscribed.");
          updateRequestSent = true;
        } else {
          Serial.println("Firmware Update FAILED to start or subscribe.");
        }
      }    
      tb.loop();
      vTaskDelay(10);
    }
   }

void taskMQTT(void* pvParameters){
    while (1){
      if (!tb.connected()){
        Serial.println("Connecting to ThingsBoard...");
        if (!tb.connect(THINGSBOARD_SERVER, TOKEN, THINGSBOARD_PORT)){
          Serial.println("Failed to connect");
          vTaskDelay(5000);
          continue;
        }else{
          Serial.println("Connected to ThingsBoard");
          // Share_attribute 

          if (!requestedShared) {
            const Attribute_Request_Callback<MAX_ATTRIBUTES> sharedCallback(&processSharedAttributeRequest, REQUEST_TIMEOUT_MICROSECONDS, &requestTimedOut, SHARED_ATTRIBUTES_LIST);
            requestedShared = attr_request.Shared_Attributes_Request(sharedCallback);
          }
          if (!subscribedShared) {
            const Shared_Attribute_Callback<MAX_ATTRIBUTES> callback(&processSharedAttributes, SHARED_ATTRIBUTES_LIST);
            subscribedShared = shared_update.Shared_Attributes_Subscribe(callback);
          }
        }
      }
      // Serial.printf("Temperature: %.2f, Humidity: %.2f\n, Light: %.2f\n", temperature, humidity, light);
      tb.loop();
      vTaskDelay(10);
    }
}

void outputControl(void* pvParameters){
  while(1){
    if(ledStateChanged){
      Serial.printf("LED state changed: %d\n", ledState);
      ledStateChanged = 0;
      digitalWrite(LED_PIN, bool(ledState) ? (HIGH) : (LOW));
      if (ledState) {
        Serial.println("LED is ON");
      } else {
        Serial.println("LED is OFF");
      }
    }
    if(fanStateChanged){
      fanStateChanged = 0;
      analogWrite(FAN_PIN, bool(fanState) ? (255) : (0));
    }
    if(servoStateChanged){
      servoStateChanged = 0;
      taskServo(servoState);
    }
    vTaskDelay(1000 / portTICK_PERIOD_MS);
  }
}

void taskSendData(void* pvParameters) {
  while (1) {
    if (tb.connected()) {
      tb.sendTelemetryData(TEMPERATURE_KEY, temperature);
      tb.sendTelemetryData(HUMIDITY_KEY, humidity);
      tb.sendTelemetryData(LIGHT_KEY, light);
      tb.sendTelemetryData(MQ2_KEY, mq2Value);

      tb.sendAttributeData(TEMPERATURE_KEY, temperature);
      tb.sendAttributeData(HUMIDITY_KEY, humidity);
      tb.sendAttributeData(LIGHT_KEY, light);
      tb.sendAttributeData(MQ2_KEY, mq2Value);

      Serial.printf("Sent telemetry data: Temp=%.2f, Humidity=%.2f, Light=%.2f, MQ2=%.2f\n", temperature, humidity, light,mq2Value);
    } else {
      Serial.println("Not connected to ThingsBoard, skipping telemetry send.");
    }
    vTaskDelay(TELEMETRY_SEND_INTERVAL / portTICK_PERIOD_MS);
  }
}

void taskFire(void* pvParameters) {
  const uint32_t FIRE_CONFIRM_TIME_MS = 3000; // 3 giây xác nhận cháy
  const uint32_t FIRE_CLEAR_TIME_MS = 3000;   // 3 giây xác nhận hết cháy
  uint32_t fireHighStart = 0;
  uint32_t fireLowStart = 0;
  fireState = 0;
  while (1) {
    int firePin = digitalRead(FIRE_DETECTION_PIN);
    // Serial.printf("Fire pin state: %d\n", firePin);
    if (firePin == 0) {
      if (fireHighStart == 0) fireHighStart = millis();
      fireLowStart = 0;
      // Nếu giữ HIGH đủ lâu và chưa gửi true
      if (!fireState && (millis() - fireHighStart >= FIRE_CONFIRM_TIME_MS)) {
        fireState = 1;
        Serial.println("Fire detected!");
        tb.sendAttributeData(FIRE_KEY, 1);
      }
    } else {
      if (fireLowStart == 0) fireLowStart = millis();
      fireHighStart = 0;
      // Nếu giữ LOW đủ lâu và đã gửi true trước đó
      if (fireState && (millis() - fireLowStart >= FIRE_CLEAR_TIME_MS)) {
        fireState = 0;
        Serial.println("Fire cleared!");
        tb.sendAttributeData(FIRE_KEY, 0);
      }
    }
    vTaskDelay(1000 / portTICK_PERIOD_MS);
  }
}

void setup() {  
  pinMode(LED_PIN,OUTPUT);
  pinMode(LIGHT_SEN_PIN, INPUT);
  pinMode(MQ2_PIN, INPUT);
  pinMode(FIRE_DETECTION_PIN, INPUT);
  Wire.begin(SDA_PIN, SCL_PIN);
  Serial.begin(SERIAL_DEBUG_BAUD);
  myServo.attach(SERVO_PIN);
  delay(1000);
  xTaskCreatePinnedToCore(taskThingsBoard, "Thingsboard Connection", 8192, NULL, 1, NULL, 1);
  xTaskCreatePinnedToCore(taskWifiConnection, "WiFi Connection", 4096, NULL, 1, NULL, 0);
  // xTaskCreatePinnedToCore(taskDHT11, "Temperature and Humidity", 4096, NULL, 1, NULL, 1);  
  // xTaskCreatePinnedToCore(taskOLED, "OLED Display", 4096, NULL, 1, NULL, 1);
  // xTaskCreatePinnedToCore(taskMQTT, "MQTT Task", 8192, NULL, 1, NULL, 1);
  // xTaskCreatePinnedToCore(taskLightSensor, "Light Sensor", 4096, NULL, 1, NULL, 1);
  // xTaskCreatePinnedToCore(taskMQ2, "MQ2 Sensor", 4096, NULL, 1, NULL, 1);
  // xTaskCreatePinnedToCore(taskSendData, "Sending Data", 4096, NULL, 1, NULL, 1);
  // xTaskCreatePinnedToCore(outputControl, "Output Control", 4096, NULL, 1, NULL, 0);
  // xTaskCreatePinnedToCore(taskFire, "Fire Detection", 4096, NULL, 1, NULL, 1);
}

void loop() {
}

