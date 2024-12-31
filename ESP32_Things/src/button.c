
#include "button.h"

// Biến lưu trữ trạng thái nút nhấn
static int buttonBuffer[NO_OF_BUTTONS] = {NORMAL_STATE, NORMAL_STATE, NORMAL_STATE};
static int Key0[NO_OF_BUTTONS] = {NORMAL_STATE, NORMAL_STATE, NORMAL_STATE};
static int Key1[NO_OF_BUTTONS] = {NORMAL_STATE, NORMAL_STATE, NORMAL_STATE};
static int Key2[NO_OF_BUTTONS] = {NORMAL_STATE, NORMAL_STATE, NORMAL_STATE};

// Các cờ và bộ đếm cho trạng thái nhấn nút
static int button_flag_Press[NO_OF_BUTTONS] = {0, 0, 0};
static int button_flag_Press1s[NO_OF_BUTTONS] = {0, 0, 0};
static int counter_button_flag_Press1s[NO_OF_BUTTONS] = {0, 0, 0};

// Các cổng và chân nút nhấn


// Kiểm tra xem nút nhấn đã được nhấn hay chưa
int is_button_pressed(int index) {
    if (button_flag_Press[index] == 1) {
        button_flag_Press[index] = 0; // Reset cờ sau khi xử lý
        return 1; // Trả về 1 nếu nút vừa được nhấn
    }
    return 0; // Không có nút nào được nhấn
}

// Kiểm tra xem nút nhấn đã được giữ hơn 1 giây hay chưa
int is_button_pressed_1s(int index) {
    if (button_flag_Press1s[index] == 1) {
        button_flag_Press1s[index] = 0; // Reset cờ sau khi xử lý
        return 1; // Trả về 1 nếu nút đã được giữ hơn 1 giây
    }
    return 0; // Không có nút nào được giữ 1 giây
}

// Đọc và xử lý trạng thái nút nhấn
void getKeyInput() {
    for (int i = 0; i < NO_OF_BUTTONS; i++) {
        Key0[i] = Key1[i];
        Key1[i] = Key2[i];
        Key2[i] = digitalRead(BUTTON_PIN); // Đọc trạng thái từ GPIO


        // Xử lý debounce bằng cách kiểm tra 3 lần liên tiếp
        if ((Key0[i] == Key1[i]) && (Key1[i] == Key2[i])) {
            if (buttonBuffer[i] != Key2[i]) { // Nếu trạng thái thay đổi
                buttonBuffer[i] = Key2[i];
                if (Key2[i] == PRESSED_STATE) {
                    button_flag_Press[i] = 1; // Đặt cờ nút nhấn
                }
            } else {
                // Tăng bộ đếm khi giữ nút
                counter_button_flag_Press1s[i]++;
                if (counter_button_flag_Press1s[i] >= DURATION_FOR_AUTO_INCREASING) {
                    if (Key2[i] == PRESSED_STATE) {
                        button_flag_Press1s[i] = 1; // Đặt cờ nút nhấn giữ lâu
                    }
                    counter_button_flag_Press1s[i] = 0; // Reset bộ đếm
                }
            }
        }
    }
}
