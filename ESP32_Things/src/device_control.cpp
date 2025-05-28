#include "device_control.h"
#include "global.h"


#define LED_COUNT 4


Adafruit_NeoPixel strip(LED_COUNT, LED_PIN, NEO_GRB + NEO_KHZ800);
int colorIndex = 0;
int colors[][3] = {
    {0,0,0},
    // {255, 0, 0},   // Đỏ
    // {0, 255, 0},   // Xanh lá
    // {0, 0, 255},   // Xanh dương
    // {255, 255, 0}, // Vàng
    // {255, 0, 255}, // Tím
    {0, 255, 255}, // Cyan
    // {255, 255, 255} // Trắng
};

DHT20 dht20;

void device_control(){
    if(buffer_device[MOTOR] == 1){
        //servo.attach(5);
        for (int pos = 0; pos <= 180; pos++) {
        servo.write(pos);
        }
    }
    else if(buffer_device[MOTOR] == 0){
        for (int pos=180; pos >= 0; pos--) {
        servo.write(pos);
        }
    }

    if(buffer_device[FAN] == 1){ //tat
        // digitalWrite(FAN_PIN, LOW);
        analogWrite(FAN_PIN, 0);
    }
    else if(buffer_device[FAN] == 0){
        // digitalWrite(FAN_PIN, HIGH);
        analogWrite(FAN_PIN, 250);
    }

    if(buffer_device[BUZZER] == 1){
        digitalWrite(BUZZER_PIN, LOW);
    }
    else if(buffer_device[BUZZER] == 0){
        digitalWrite(BUZZER_PIN, LOW);
    }

    if(buffer_device[LED] == 1){
        // digitalWrite(LED_PIN, HIGH);
        colorIndex = (colorIndex + 1) % 2;
        for (int i = 0; i < LED_COUNT; i++) {
            strip.setPixelColor(i, strip.Color(0, 0, 0));
        }
        strip.show();
    }
    else if(buffer_device[LED] == 0){
        // digitalWrite(LED_PIN, LOW);
        colorIndex = (colorIndex + 1) % 2;
        for (int i = 0; i < LED_COUNT; i++) {
            strip.setPixelColor(i, strip.Color(colors[colorIndex][0],  colors[colorIndex][1],  colors[colorIndex][2]));
        }
        strip.show();
    }
}

