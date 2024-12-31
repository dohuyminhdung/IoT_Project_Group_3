#include <Arduino.h>
#include "scheduler.h"
#include "global.h"
// Define maximum tasks
sTask SCH_tasks_G[SCH_MAX_TASKS];
uint8_t current_index_task = 0;

void SCH_Init() {
    current_index_task = 0;
    for (int i = 0; i < SCH_MAX_TASKS; i++) {
        SCH_Delete_Task(i);
    }
}

void SCH_Delete_Task(int task_index) {
    SCH_tasks_G[task_index].pTask = NULL;
    SCH_tasks_G[task_index].Delay = 0;
    SCH_tasks_G[task_index].Period = 0;
    SCH_tasks_G[task_index].RunMe = 0;
    SCH_tasks_G[task_index].TaskID = 0;
}

void SCH_Add_Task(void (*pFunction)(), uint32_t DELAY, uint32_t PERIOD) {
    if (current_index_task < SCH_MAX_TASKS) {
        SCH_tasks_G[current_index_task].pTask = pFunction;
        SCH_tasks_G[current_index_task].Delay = DELAY;
        SCH_tasks_G[current_index_task].Period = PERIOD;
        SCH_tasks_G[current_index_task].RunMe = 0;
        SCH_tasks_G[current_index_task].TaskID = current_index_task;
        current_index_task++;
    }
}

void SCH_Update() {
    for (int i = 0; i < current_index_task; i++) {
        if (SCH_tasks_G[i].pTask) {
            if (SCH_tasks_G[i].Delay > 0) {
                SCH_tasks_G[i].Delay--;
            } else {
                SCH_tasks_G[i].RunMe += 1;
                if (SCH_tasks_G[i].Period) {
                    SCH_tasks_G[i].Delay = SCH_tasks_G[i].Period;
                }
            }
        }
    }
}

void SCH_Dispatch_Tasks() {
    for (int i = 0; i < current_index_task; i++) {
        if (SCH_tasks_G[i].RunMe > 0) {
            SCH_tasks_G[i].RunMe--;
            (*SCH_tasks_G[i].pTask)();
        }
    }
}
