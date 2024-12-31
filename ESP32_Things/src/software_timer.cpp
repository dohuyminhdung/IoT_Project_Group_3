
#include "software_timer.h"


int timer_counter[5];
int timer_flag[5];

void setTimer(int index, int counter){
	timer_counter[index] = counter;
	timer_flag[index] = 0;
}

void timerRun(){
	for(int i = 0; i < 5; i++){
		if(timer_counter[i] > 0){
			timer_counter[i]--;
			if(timer_counter[i] <= 0){
			timer_flag[i] = 1;
			}
		}
	}
}

