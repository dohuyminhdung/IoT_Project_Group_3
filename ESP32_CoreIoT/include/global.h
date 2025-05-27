#ifndef INC_GLOBAL_H
#define INC_GLOBAL_H

#include <Arduino.h>
#include <ThingsBoard.h>
#include <WiFi.h>
#include <Arduino_MQTT_Client.h>
#include <OTA_Firmware_Update.h>
#include <Shared_Attribute_Update.h>
#include <Attribute_Request.h>
#include <Espressif_Updater.h>
#include <DHT.h>
#include <Wire.h>
#include "RPC_Callback.h"
#include <Adafruit_SSD1306.h>
#include <ESP32Servo.h>
#include <MQUnifiedsensor.h>



#include <sensor.h>
#include <oled.h>


#define CURRENT_FIRMWARE_TITLE "OTA_Lab3"
#define CURRENT_FIRMWARE_VERSION "1.0.1"
#define SDA_PIN GPIO_NUM_11
#define SCL_PIN GPIO_NUM_12
#define DHT_PIN GPIO_NUM_6
#define LED_PIN GPIO_NUM_48
#define LIGHT_SEN_PIN GPIO_NUM_8
#define FAN_PIN GPIO_NUM_10
#define SERVO_PIN GPIO_NUM_5
#define MQ2_PIN GPIO_NUM_3
#define FIRE_DETECTION_PIN GPIO_NUM_18

#define VCC 3.3
#define RL 1.0
#define Ro 10.0 // Điện trở chuẩn của cảm biến MQ2


extern volatile bool ledState;
extern volatile bool ledStateChanged;
extern volatile bool fanState;
extern volatile bool fanStateChanged;
extern volatile bool servoState;
extern volatile bool servoStateChanged;

extern volatile bool fireState;

extern float temperature;
extern float humidity;
extern float light;
extern float mq2Value;

#endif // INC_GLOBAL_H