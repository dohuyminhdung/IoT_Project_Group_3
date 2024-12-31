#include <Wire.h>
#include <i2c_lcd.h>
#define SLAVE_ADDRESS_LCD 0x21 // Địa chỉ I2C của LCD (sử dụng địa chỉ phù hợp với LCD của bạn)

void display_lcd(float num){
    char buffer[6];
    sprintf(buffer, "%.2f", num);
    buffer[5] = ' ';
    lcd_goto_XY(0, 10);
    lcd_send_string(buffer);
}
void lcd_send_cmd(char cmd) {
  char data_u, data_l;
  uint8_t data_t[4];
  
  data_u = (cmd & 0xf0);
  data_l = ((cmd << 4) & 0xf0);
  
  data_t[0] = data_u | 0x0C;  // en=1, rs=0
  data_t[1] = data_u | 0x08;  // en=0, rs=0
  data_t[2] = data_l | 0x0C;  // en=1, rs=0
  data_t[3] = data_l | 0x08;  // en=0, rs=0
  
  Wire.beginTransmission(SLAVE_ADDRESS_LCD);  // Bắt đầu truyền dữ liệu I2C đến LCD
  Wire.write(data_t, 4);  // Gửi 4 byte dữ liệu
  Wire.endTransmission(); // Kết thúc truyền
}

void lcd_send_data(char data) {
  char data_u, data_l;
  uint8_t data_t[4];
  
  data_u = (data & 0xf0);
  data_l = ((data << 4) & 0xf0);
  
  data_t[0] = data_u | 0x0D;  // en=1, rs=1
  data_t[1] = data_u | 0x09;  // en=0, rs=1
  data_t[2] = data_l | 0x0D;  // en=1, rs=1
  data_t[3] = data_l | 0x09;  // en=0, rs=1
  
  Wire.beginTransmission(SLAVE_ADDRESS_LCD);  // Bắt đầu truyền dữ liệu I2C đến LCD
  Wire.write(data_t, 4);  // Gửi 4 byte dữ liệu
  Wire.endTransmission(); // Kết thúc truyền
}

void lcd_init(void) {
  lcd_send_cmd(0x33); // Set 4-bit interface
  lcd_send_cmd(0x32);
  delay(50);
  lcd_send_cmd(0x28); // Set LCD function
  delay(50);
  lcd_send_cmd(0x01); // Clear display
  delay(50);
  lcd_send_cmd(0x06); // Set entry mode
  delay(50);
  lcd_send_cmd(0x0C); // Display on
  delay(50);
  lcd_send_cmd(0x02); // Move cursor to home
  delay(50);
  lcd_send_cmd(0x80); // Set cursor to beginning
}

void lcd_send_string(char *str) {
  while (*str) {
    lcd_send_data(*str++);
  }
}

void lcd_clear_display(void) {
  lcd_send_cmd(0x01); // Clear display
}

void lcd_goto_XY (int row, int col)
{
	uint8_t pos_Addr;
	if(row == 0) 
	{
		pos_Addr = 0x80 + col;
	}
	else
	{
		pos_Addr = 0x80 | (0x40 + col);
	}
	lcd_send_cmd(pos_Addr);
}
