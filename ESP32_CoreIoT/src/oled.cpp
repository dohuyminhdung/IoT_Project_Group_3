#include <oled.h>

// OLED
Adafruit_SSD1306 display(SCREEN_WIDTH, SCREEN_HEIGHT, &Wire, OLED_RESET);

// OLED display
void taskOLED(void *pvParameters)
{
  if(!display.begin(SSD1306_SWITCHCAPVCC, SCREEN_ADDRESS)) {
    Serial.println(F("SSD1306 allocation failed"));
    for(;;); // Loop
  }
    Serial.println(F("SSD1306 allocation successful"));
  display.display();
  delay(2000); // Pause for 2 seconds
  
  // // Draw a single pixel in white
  // display.drawPixel(10, 10, SSD1306_WHITE);

  // // Show the display buffer on the screen. You MUST call display() after
  // // drawing commands to make them visible on screen!
  // display.display();
  while (1)
  {
    display.clearDisplay();
    display.setTextSize(1);
    display.setTextColor(SSD1306_WHITE);
    if(fireState == 1){
      display.clearDisplay();
      display.setCursor(10, 0);
      display.println("Fire detected!");
      display.display();
      delay(3000);
    } else{
      display.clearDisplay();
      display.setCursor(0, 0);
      display.printf("Temp: %.1f C", temperature);
      display.setCursor(0, 10);
      display.printf("Humidity: %.1f %%", humidity);
      display.setCursor(0, 20);
      display.printf("Light: %.1f Lux", light);
      display.display();
      display.setCursor(0, 30);
      display.printf("MQ2: %.1f ppm", mq2Value);
    }
  }
}