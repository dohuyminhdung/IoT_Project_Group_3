#include <global.h>


volatile bool ledState = false;
volatile bool ledStateChanged = false;
volatile bool fanState = false;
volatile bool fanStateChanged = false;
volatile bool servoState = false;
volatile bool servoStateChanged = false;


volatile bool fireState = false;

float temperature = 0.0f;
float humidity = 0.0f;
float light = 0.0f;
float mq2Value = 0.0f;