#include "global.h"
#include "fsm.h"
#include <Keypad.h>
float buffer_data[5] = {-1};
int status = INIT;
int timelcd;
char input[5];       // Lưu chuỗi nhập từ bàn phím
int input_index = 0; // Chỉ số ký tự trong chuỗi nhập
void fsm_lcd(){
    switch(status){
        case INIT:
            lcd_init();
            timelcd = TIMELCD;
            status = NHIET_DO;
            break;
        case NHIET_DO:
            timelcd--;
            // goi ham lcd
            lcd_goto_XY(0, 0);
            lcd_send_string((char*)"Nhiet do: ");
            display_lcd(buffer_data[0]);
            if(timelcd <= 0){
                status = DO_AM;
                timelcd = TIMELCD;
            }
            if(flag_key){
                lcd_clear_display();
                lcd_init();
                status = INIT_PASSWORD;
            }
            if(timer_flag[1] == 1) buffer_device[MOTOR] = 0;
            break;

        case DO_AM:
            timelcd--;
            lcd_goto_XY(0, 0);
            lcd_send_string((char*)"Do am:    ");
            display_lcd(buffer_data[1]);
            if(timelcd <= 0){
                status = ANH_SANG;
                timelcd  = TIMELCD;
            }
            if(flag_key){
                lcd_clear_display();
                status = INIT_PASSWORD;
                lcd_init();            
            }
            if(timer_flag[1] == 1) buffer_device[MOTOR] = 0;
            break;
        case ANH_SANG:
            timelcd--;
            lcd_goto_XY(0, 0);
            lcd_send_string((char*)"Anh sang: ");
            display_lcd(buffer_data[2]);
            if(timelcd <= 0){
                status = NHIET_DO;
                timelcd = TIMELCD;
            }
            if(flag_key){
                lcd_clear_display();
                status = INIT_PASSWORD;
                lcd_init();
            }
            if(timer_flag[1] == 1) buffer_device[MOTOR] = 0;
            break;
        default:
            break;
    }
}

void fsm_password(){
    switch(status){
        case INIT_PASSWORD:
            lcd_goto_XY(0, 0);
            lcd_send_string((char*)"Nhap mat khau:");
            if(key){
                status = READ_PASSWORD;
            }
        case READ_PASSWORD:
                if (key == '#') { // Kết thúc nhập khi nhấn #
                    if (strcmp(input, "1234") == 0) { // So sánh với mật khẩu
                        lcd_goto_XY(1, 0);
                        lcd_send_string((char*)"Mat khau dung");
                        buffer_device[MOTOR] = 1;
                        setTimer(0, 2);
                    } else {
                        lcd_goto_XY(1, 0);
                        lcd_send_string((char*)"Sai! Thu lai.");
                        input_index = 0;
                        memset(input, 0, sizeof(input));
                        lcd_clear_display();
                        lcd_init();
                        lcd_goto_XY(0, 0);
                        lcd_send_string((char*)"Nhap mat khau:");
                    }
                } else if (key >= '0' && key <= '9') {
                    if (input_index < 4) {
                        input[input_index] = key;
                        input_index++;
                        lcd_goto_XY(1, 0);
                        lcd_send_string(input); 
                    }
                } else if (key == '*') {
                    lcd_clear_display();
                    lcd_init();
                    input_index = 0;
                    memset(input, 0, sizeof(input));
                    status = INIT_PASSWORD;
                }
                if(timer_flag[0] == 1){
                    timer_flag[0] = 0;
                    setTimer(1, 2);
                    buffer_device[MOTOR] = 1;
                    input_index = 0; 
                    memset(input, 0, sizeof(input));
                    status = INIT; 
                    lcd_clear_display();
                    lcd_init();
                }                

            break;

        default:
            break;
    }
}
void key_is_press(){
    key = keypad.getKey();
    if(key){
    if(key == '*'){
        flag_key = 1;
    }    
    else flag_key = 0;
    }
}