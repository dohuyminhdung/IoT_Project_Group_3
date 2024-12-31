/*
 * scheduler.h
 *
 *  Created on: Nov 4, 2024
 *      Author: ASUS
 */

#ifndef INC_SCHEDULER_H_
#define INC_SCHEDULER_H_

#include <stdint.h>

// Struct định nghĩa nhiệm vụ
typedef struct {
    void (*pTask)(void);    // Con trỏ tới hàm nhiệm vụ
    uint32_t Delay;         // Thời gian trễ trước khi chạy
    uint32_t Period;        // Chu kỳ lặp lại
    uint8_t	RunMe;          // Số lần nhiệm vụ cần chạy
    uint32_t TaskID;        // ID nhiệm vụ
} sTask;

// Cấu hình scheduler
#define SCH_MAX_TASKS 7     // Số lượng nhiệm vụ tối đa
#define NO_TASK_ID	  0     // Không có nhiệm vụ ID

// Biến toàn cục
extern sTask SCH_tasks_G[SCH_MAX_TASKS];  // Danh sách nhiệm vụ
extern uint8_t current_index_task;       // Chỉ số nhiệm vụ hiện tại
extern uint8_t key_index;                // Biến đếm nhiệm vụ phím

// Hàm khởi tạo scheduler
void SCH_Init(void);

// Thêm một nhiệm vụ mới
void SCH_Add_Task(void (*pFunction)(), uint32_t DELAY, uint32_t PERIOD);

// Cập nhật scheduler (giảm Delay và thiết lập RunMe)
void SCH_Update(void);


// Chạy các nhiệm vụ đã sẵn sàng
void SCH_Dispatch_Tasks(void);

// Xóa nhiệm vụ khỏi danh sách
void SCH_Delete_Task(int task_index);

// Chèn nhiệm vụ vào vị trí chỉ định
void SCH_Insert_Task(sTask newTask);

#endif /* INC_SCHEDULER_H_ */
