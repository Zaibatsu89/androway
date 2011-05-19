/*
  Bot.h - Class for interacting with the physical balancing bot.
 */

#ifndef Bot_h
#define Bot_h

#include <AFMotor.h>
#include <WProgram.h>

#define SENSOR_1_PIN 6  // Pin for our first sensor
#define SENSOR_2_PIN 7  // Pin for our second sensor

// The BotData object used for storing the data of the bot
typedef struct BotData
{
  float drivingDirection;
  float drivingSpeed;
  boolean isOnHold;
};

class Bot
{
  private:
    BotData _data;  // The private BotData _data for storing the data of the bot
    AF_DCMotor _leftMotor;
    AF_DCMotor _rightMotor;
    
  public:
    // The constructor for the class, initialize the left and right motor used for the motion of the bot
    Bot(short leftMotorPin, short rightMotorPin) : _leftMotor(leftMotorPin, MOTOR12_1KHZ), _rightMotor(rightMotorPin, MOTOR12_1KHZ)
    {
      // Initialize some variables that are used by the class
      BotData _data = {};
      _data.isOnHold = true;  // The bot is set on hold by default
      
      // Set the speed for both motors
      _leftMotor.setSpeed(150);
      _rightMotor.setSpeed(150);
      
      // Set the direction for both motors
//      _leftMotor.run(BACKWARD);
//      _rightMotor.run(BACKWARD);
    };

    struct BotData getData(){ return _data; };  // Get the data currently used by the bot
    void setData(struct BotData botData);       // Set the new data for the bot
    void handleMotion();  // Handles the motion/driving of the bot. Triggers the engines.
};

#endif
