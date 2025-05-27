#include <sensor.h>

DHT dht11(DHT_PIN, DHT11); //DHT11

void taskDHT11(void * pvParameters){
  dht11.begin();
  while (true)
  {
    temperature = dht11.readTemperature();
    humidity = dht11.readHumidity();
    if (!isnan(temperature) && !isnan(humidity))
    {
      // Serial.printf("Temp: %.2f C, Humidity: %.2f%%\n", temperature, humidity);
      // tb.sendTelemetryData("temperature", temperature);
      // tb.sendTelemetryData("humidity", humidity);
      // tb.sendAttributeData("temperature", temperature);
      // tb.sendAttributeData("humidity", humidity);
    }
    else
    {
      Serial.println("Failed to read DHT11");
    }
    vTaskDelay(1000);
  }
}

void taskLightSensor(void * pvParameters) {
  while (true) {
    light = analogRead(LIGHT_SEN_PIN);
    // Serial.printf("Light: %.2f\n", light);
    vTaskDelay(1000);
  }
}

void taskMQ2(void * pvParameters) {
  while (1) {
    int adcValue = analogRead(MQ2_PIN) % 2000;
    float Vout = (adcValue * VCC) / 4095.0;
    float Rs = RL * ((VCC - Vout) / Vout);
    float ratio = Rs / Ro; // Rs/Ro

    // Tính toán ppm theo công thức của LPG
    mq2Value = pow(10, (-2.862 * log10(ratio) + 1.578));
    Serial.printf("MQ Sensor Value: %.2f ppm", mq2Value);
    Serial.printf("ADC Value: %d\n", adcValue);

    vTaskDelay(1000);
  }
}