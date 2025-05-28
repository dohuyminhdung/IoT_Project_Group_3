#include"read_write_data.h"
#include "global.h"

int fire_value;
int gas_value;
float temperature;
float humidity;
float rawLight;

void sendDHT_temp() {
  dht20.read();
  temperature = dht20.getTemperature();
  //dht.readTemperature();  // Đọc nhiệt độ
    // if (isnan(temperature)) {
    //     Serial.println("!error:Sensor:Invalid#");  
    //     return; 
    // }
    buffer_data[0] = temperature;
    char message[20];
    snprintf(message, sizeof(message), "!sensor1:T:%.1f#", temperature);
    Serial.println(message);
}

void sendDHT_humi() {
    // Đọc độ ẩm
    dht20.read();
    humidity = dht20.getHumidity();
    // dht.readHumidity();
    buffer_data[1] = humidity; 
    // if (isnan(humidity)) {
    //     Serial.println("!error:Sensor:Invalid#");
    //     return; 
    // }
    char message[20];
    snprintf(message, sizeof(message), "!sensor2:H:%.1f#", humidity);
    Serial.println(message);
}

void send_light() {
    // Đọc giá trị ánh sáng từ chân analog
    rawLight = analogRead(LIGHTPIN); // Đọc giá trị ánh sáng (0-4095)
    // lightPercent = map(rawLight, 4096, 0, 0, 100); // Chuyển đổi sang phần trăm (%)
    buffer_data[2] = rawLight;
    // lightPercent;
    char message[20];
    snprintf(message, sizeof(message), "!sensor3:L:%.1f#", rawLight);
    //  lightPercent);
    Serial.println(message);
}

void send_fire(){
  fire_value = digitalRead(FIRE_PIN);


  if(fire_value == LOW){
    buffer_device[BUZZER] = 1;
    //buffer_device[FAN] = 1;
   // setTimer(2, 10);
    char message1[20];
    snprintf(message1, sizeof(message1), "!sensor4:Q:%d#", 1);
    Serial.println(message1);

    buffer_data[3] == fire_value;
    char message[20];
    snprintf(message, sizeof(message), "!sensor4:F:%d#", fire_value);
    Serial.println(message);
  }

  else if(fire_value == HIGH && gas_value == HIGH){
    buffer_device[BUZZER] = 0;
  }

}

void send_gas(){
  gas_value = digitalRead(GAS_PIN);
  if(gas_value == LOW){
    buffer_device[BUZZER] = 1;
    buffer_device[FAN] = 1;
    //setTimer(2, 10);
    char message1[20];
    snprintf(message1, sizeof(message1), "!sensor5:Q:%d#", 1);
    Serial.println(message1);


    buffer_data[4] = gas_value;
    char message[20];
    snprintf(message, sizeof(message), "!sensor5:G:%d#", gas_value);
    Serial.println(message);
  }
  else if(fire_value == HIGH && gas_value == HIGH){
    buffer_device[BUZZER] = 0;
  }


}


//LCD cua Huy
LiquidCrystal_I2C lcd(0x21, 16, 2);
void TaskLCD_Display() {
    lcd.clear();
    lcd.setCursor(0, 0);
    lcd.print(temperature, 1);lcd.print(" oC");
    lcd.setCursor(8,0);
    lcd.print(humidity, 1); lcd.print(" %");

    lcd.setCursor(0, 1);
    lcd.print(rawLight,1); 
    lcd.setCursor(5,1); lcd.print("lx");
    lcd.setCursor(8,1); 
    lcd.print("00.0"); lcd.print(" AQI");
}

void read_serial_data() {
  // Kiểm tra xem có dữ liệu chưa
  if (Serial.available() > 0) {
    String input = Serial.readString(); 
    
    Serial.print("Received: ");
    Serial.println(input);
    
    // Kiểm tra xem chuỗi có đúng định dạng hay không
    if (input.startsWith("!B:") && input.endsWith("#")) {

      int first_colon = input.indexOf(':');
      int second_colon = input.indexOf(':', first_colon + 1);
      int last_hash = input.indexOf('#');
      
      if (first_colon != -1 && second_colon != -1 && last_hash != -1) {
        String indexStr = input.substring(first_colon + 1, second_colon);
        int index = indexStr.toInt();
        String dataStr = input.substring(second_colon + 1, last_hash);
        int data = dataStr.toInt();          
        buffer_device[index - 1] = data;
      } 
    } else {
      Serial.println("Invalid data format.");
    }
  }
}