#ifndef INC_SENSOR_H
#define INC_SENSOR_H

#include <global.h>

void taskDHT11(void * pvParameters);
void taskLightSensor(void * pvParameters);
void taskMQ2(void * pvParameters);

#endif // INC_SENSOR_H