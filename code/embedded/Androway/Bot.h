/*
  Boat.cpp - Class for interacting with the physical boat.
*/

#ifndef Bot_h
#define Bot_h

#include <WProgram.h>

#define SENSOR_1_PIN 6  // Pin for our first sensor
#define SENSOR_2_PIN 7  // Pin for our second sensor


class Bot
{
  public:
    Bot();  // Empty constructor for the class
    
    uint8_t getDataElm1(){return _dataElm1;};  // Get data element 1
    uint8_t getDataElm2(){return _dataElm1;};  // Get data element 2
    uint8_t getDataElm3(){return _dataElm1;};  // Get data element 3
    
    void setDataElm1(uint8_t dataElm);         // Set data element 1
    void setDataElm2(uint8_t dataElm);         // Set data element 2
    void setDataElm3(uint8_t dataElm);         // Set data element 3
    
  private:
    _dataElm1 = 0;  // Future data elements
    _dataElm2 = 0;  // Future data elements
    _dataElm3 = 0;  // Future data elements
};

#endif
