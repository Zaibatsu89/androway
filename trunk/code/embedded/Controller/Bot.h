/*
  Bot.h - Class for interacting with the physical balancing bot.
 */

#ifndef Bot_h
#define Bot_h

#include <AFMotor.h>
#include <WProgram.h>

#define SENSOR_1_PIN 6  // Pin for our first sensor
#define SENSOR_2_PIN 7  // Pin for our second sensor
#define VOLTAGE_AVERAGE_SPREAD 30  // The number of values to use for an average calculation

// The IncomingData object used for storing the received data
typedef struct IncomingData
{
  float drivingDirection;
  float drivingSpeed;
  boolean do360;
  boolean isOnHold;
  boolean stopSession;
};

// The OutgoingData object used for storing the data that needs to be sent
typedef struct OutgoingData
{
  int leftWheel;
  int rightWheel;
  float inclination;
  int batteryVoltage;
};

class Bot
{
  private:
    // Private variables
    IncomingData _incomingData;  // The private IncomingData _incomingData for storing the received data
    OutgoingData _outgoingData;  // The private OutgoingData _outgoingData for storing the data that needs to be sent
    AF_DCMotor _leftMotor;
    AF_DCMotor _rightMotor;
    byte _leftMotorNr;
    byte _rightMotorNr;
    byte _batteryVoltagePort;
    boolean _motorRunning;
    
    // Private functions
    void handleMotion();        // Handles the motion/driving of the bot. Triggers the engines.
    int getBatteryVoltage();   // Retrieves the battery voltage
    
  public:
    // The constructor for the class, initialize the left and right motor used for the motion of the bot    
    Bot(byte leftMotorNr, byte rightMotorNr, byte batteryVoltagePort) : _leftMotor(leftMotorNr, MOTOR12_1KHZ), _rightMotor(rightMotorNr, MOTOR12_1KHZ)
    {
      // Initialize some variables that are used by the class
      IncomingData _incomingData = {};
      _incomingData.isOnHold = true;  // The bot is set on hold by default
      _incomingData.do360 = false;    // The bot doesn't do a 360 by default
      
      OutgoingData _outgoingData = {};
      
      _leftMotorNr = leftMotorNr;
      _rightMotorNr = rightMotorNr;
      _batteryVoltagePort = batteryVoltagePort;
      
      _motorRunning = false;
    };

    struct OutgoingData getOutgoingData();              // Get the data outgoing data
    void setIncomingData(struct IncomingData botData);  // Set the received data for the bot
    void stopRunningSession();  // Handles the stopping of the current session
    float mapFloat(float x, float in_min, float in_max, float out_min, float out_max);  // A version of the Arduino map() function that accepts floats
};

#endif
