/*
  Boat.cpp - Class for interacting with the physical boat.
*/

#ifndef Bot_h
#define Bot_h

#include <WProgram.h>

#define SENSOR_1_PIN 6  // Pin for our first sensor
#define SENSOR_2_PIN 7  // Pin for our second sensor

// The BotData object used for storing the data of the bot
typedef struct BotData
{
  float drivingDirection;
  int drivingSpeed;
  boolean isOnHold;
};

class Bot
{
  private:
    BotData _data;  // The private BotData _data for storing the data of the bot
    
  public:
    // The constructor for the class, store the NewSoftSerial instance and the pin numbers in private variables.
    Bot()
    {
      // Initialize some variables that are used by the class
      BotData _data = {};
      _data.isOnHold = true;  // The bot is set on hold by default
    };

    struct BotData getData(){ return _data; };  // Get the data currently used by the bot
    void setData(struct BotData botData);       // Set the new data for the bot
};

#endif
