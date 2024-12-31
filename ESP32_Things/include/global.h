/*
 * global.h
 *
 *  Created on: Oct 27, 2024
 *      Author: ASUS
 */

#include "driver/gpio.h"       // Thư viện GPIO cho ESP32
#include "scheduler.h"
#include "Arduino.h"
#include "main.h"
#include <Keypad.h>
#include <ESP32Servo.h> 

#include "DHT.h"
#include "scheduler.h"
#include <Wire.h>
#include "i2c_lcd.h"    
#include "read_write_data.h"
#include "button.h"
#include "software_timer.h"
#include "fsm.h"
#include "device_control.h"

// Định nghĩa trạng thái của hệ thống
#define INIT 0
#define NHIET_DO 1
#define DO_AM 2
#define ANH_SANG 3
#define INIT_PASSWORD 4
#define READ_PASSWORD 5

#define TIMELCD 5
#define ROW  4
#define COL  4

#define DHTPIN  15
#define LIGHTPIN 2
#define GAS_PIN 4
#define FIRE_PIN 16
#define DHTTYPE DHT11
#define LED_PIN 17
#define FAN_PIN 5
#define MOTOR_PIN 23
#define BUZZER_PIN 18

#define MAX_DEVICE  4
#define LED     0
#define FAN     1
#define MOTOR   2
#define BUZZER  3
#define SLAVE_ADDRESS_LCD 0x21 // Địa chỉ I2C của LCD (sử dụng địa chỉ phù hợp với LCD của bạn)


extern int status;
extern bool ledState;
extern int buffer_device[MAX_DEVICE] ;
extern float buffer_data[5];
extern DHT dht;
extern Servo servo;

extern char key_map[ROW][COL];
extern Keypad keypad ;
extern char key;
extern char pre_key;
extern int flag_key;

extern uint8_t rowPins[ROW];
extern uint8_t colPins[COL];