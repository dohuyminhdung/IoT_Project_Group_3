/*
 * global.c
 *
 *  Created on: Oct 27, 2024
 *      Author: ASUS
 */



#include "global.h"

bool ledState = false;
int buffer_device[MAX_DEVICE] = {-1};
char key_map[ROW][COL] = {
  {'1','2','3', 'A'},
  {'4','5','6', 'B'},
  {'7','8','9', 'C'},
  {'*','0','#', 'D'}
};
uint8_t rowPins[ROW] = {13,12, 14, 27}; // GIOP14, GIOP27, GIOP26, GIOP25
uint8_t colPins[COL] = { 26, 25, 33, 32}; // GIOP33, GIOP32, GIOP18, GIOP19
Keypad keypad = Keypad(makeKeymap(key_map), rowPins, colPins, ROW, COL );
char key = keypad.getKey();
char pre_key = key;
int flag_key = 0;