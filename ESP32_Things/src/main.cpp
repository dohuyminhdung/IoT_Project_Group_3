#include "global.h"

DHT dht(DHTPIN, DHTTYPE);
hw_timer_t *timer = NULL;  // Khởi tạo con trỏ cho timer
Servo servo;
void IRAM_ATTR onTimer() {
  // Hàm này sẽ được gọi mỗi khi timer ngắt
  //Serial.println("timer");
  timerRun();
  SCH_Update();
}

//------------------------------------------------------------------------------------//

//-------------------------------------------------------------------------------------
void setup() {
//---------------------------------SETUP PIN-----------------------------------//

  pinMode(DHTPIN, INPUT);
  pinMode(LIGHTPIN, INPUT);
  pinMode(GAS_PIN, INPUT);
  pinMode(FIRE_PIN, INPUT);
  pinMode(LED_PIN, OUTPUT); 
  pinMode(FAN_PIN, OUTPUT);
  pinMode(MOTOR_PIN, OUTPUT);
  pinMode(BUZZER_PIN, OUTPUT);
  pinMode(12, OUTPUT);
  pinMode(13, OUTPUT);

  //---------------------------------INIT FUNC----------------------------------//
  Serial.begin(9600);
  Wire.begin();
  lcd_init();
  dht.begin();         // Khởi động cảm biến DHT11
  servo.attach(MOTOR_PIN); // Gắn servo vào chân số 9
  servo.write(90); // Điều khiển servo quay đến góc 90 độ
  Serial.println("HELLO");



  //---------------------------------SCHEDULER-----------------------------------//
  SCH_Init();
    //SCH_Add_Task(toggleLED, 0, 1);
  SCH_Add_Task(sendDHT_temp, 0, 51);
  SCH_Add_Task(sendDHT_humi, 17, 51);
  SCH_Add_Task(send_light, 34, 51);
  SCH_Add_Task(send_gas, 3, 3);
  SCH_Add_Task(send_fire, 1, 3);
  SCH_Add_Task(fsm_lcd, 0, 1);
  SCH_Add_Task(device_control, 3, 1);

  
// ------------------------------------ INIT TIMER---------------------------------//
  timer = timerBegin(0, 80, true);
  // Gắn hàm onTimer cho timer interrupt
  timerAttachInterrupt(timer, &onTimer, true);
  // Đặt tần số ngắt 1 giây (1.000.000 micro giây)
  timerAlarmWrite(timer, 1000000, true);  // Ngắt mỗi 1 giây
  // Bật ngắt cho timer
  timerAlarmEnable(timer);
//-------------------------------------END TIMER---------------------------------//

}

void loop() {  
  SCH_Dispatch_Tasks();
  read_serial_data();
  key_is_press();
  fsm_password();
}
