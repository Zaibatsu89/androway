/*
  Bot.h - Class for interacting with the physical balancing bot.
 */

#ifndef Bot_h
#define Bot_h

#include <AFMotor.h>
#include <WProgram.h>

#define SENSOR_1_PIN 6  // Pin for our first sensor
#define SENSOR_2_PIN 7  // Pin for our second sensor

// The IncomingData object used for storing the received data
typedef struct IncomingData
{
  float drivingDirection;
  float drivingSpeed;
  boolean isOnHold;
  boolean stopSession;
};

// The OutgoingData object used for storing the data that needs to be sent
typedef struct OutgoingData
{
  int leftWheel;
  int rightWheel;
  float inclination;
};

class Bot
{
  private:
    IncomingData _incomingData;  // The private IncomingData _incomingData for storing the received data
    OutgoingData _outgoingData;  // The private OutgoingData _outgoingData for storing the data that needs to be sent
    AF_DCMotor _leftMotor;
    AF_DCMotor _rightMotor;
    short _leftMotorNr;
    short _rightMotorNr;
    
  public:
    // The constructor for the class, initialize the left and right motor used for the motion of the bot
    Bot(short leftMotorNr, short rightMotorNr) : _leftMotor(leftMotorNr, MOTOR12_1KHZ), _rightMotor(rightMotorNr, MOTOR12_1KHZ)
    {
      // Initialize some variables that are used by the class
      IncomingData _incomingData = {};
      _incomingData.isOnHold = true;  // The bot is set on hold by default
      
      OutgoingData _outgoingData = {};
      
      _leftMotorNr = leftMotorNr;
      _rightMotorNr = rightMotorNr;
    };

    struct OutgoingData getOutgoingData(){ return _outgoingData; };  // Get the data outgoing data
    void setIncomingData(struct IncomingData botData);               // Set the received data for the bot
    void handleMotion();        // Handles the motion/driving of the bot. Triggers the engines.
    void stopRunningSession();  // Handles the stopping of the current session
};

#endif
