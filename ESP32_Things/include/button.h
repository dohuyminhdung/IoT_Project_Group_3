/*
 * button.h
 *
 *  Created on: Oct 23, 2024
 *      Author: ASUS
 */

#ifndef INC_BUTTON_H_
#define INC_BUTTON_H_

#include "main.h" // Đảm bảo file main.h có chứa các khai báo cơ bản như GPIO hoặc các định nghĩa liên quan.

// Trạng thái nút nhấn
#define NORMAL_STATE        1      // Trạng thái khi nút nhấn không được nhấn (tùy theo mạch)
#define PRESSED_STATE       0    // Trạng thái khi nút nhấn được nhấn

// Số lượng nút nhấn trong ứng dụng
#define NO_OF_BUTTONS       3                 // Định nghĩa số lượng nút nhấn (tùy chỉnh theo nhu cầu)
#define BUTTON_PIN          35
// Thời gian giữ nút để kích hoạt auto-increment (đơn vị ms)
#define DURATION_FOR_AUTO_INCREASING 20       // Giá trị thời gian tùy chỉnh

// Các hàm xử lý nút nhấn
int is_button_pressed(int index);             // Kiểm tra nút nhấn có được nhấn hay không
int is_button_pressed_1s(int index);          // Kiểm tra nút nhấn được giữ trong 1 giây
void subKeyProcess();                         // Xử lý sự kiện phụ khi nút được nhấn
void getKeyInput();                           // Đọc giá trị nút nhấn và xử lý trạng thái

#endif /* INC_BUTTON_H_ */
