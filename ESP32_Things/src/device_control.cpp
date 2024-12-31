#include "device_control.h"
#include "global.h"

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

    if(buffer_device[FAN] == 1){
        digitalWrite(FAN_PIN, LOW);
    }
    else if(buffer_device[FAN] == 0){
        digitalWrite(FAN_PIN, HIGH);
    }

    if(buffer_device[BUZZER] == 1){
        digitalWrite(BUZZER_PIN, LOW);
    }
    else if(buffer_device[BUZZER] == 0){
        digitalWrite(BUZZER_PIN, HIGH);
    }

    if(buffer_device[LED] == 1){
        digitalWrite(LED_PIN, HIGH);
    }
    else if(buffer_device[LED] == 0){
        digitalWrite(LED_PIN, LOW);
    }
}

