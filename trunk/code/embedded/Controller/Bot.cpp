/*
  Bot.cpp - Class for interacting with the physical balancing bot.
 */

#include <AFMotor.h>
#include "Bot.h"

// Get the data outgoing data
struct OutgoingData Bot::getOutgoingData()
{
  // Get the current battery voltage and store it in the outgoing data, before it is returned
  _outgoingData.batteryVoltage = getBatteryVoltage();
  
  return _outgoingData;
}

// Set the new data for the bot
void Bot::setIncomingData(struct IncomingData botData)
{
  // Store the given IncomingData as the new data for the bot
  _incomingData = botData;

  // After the new data is set, handle the motion of the bot
  handleMotion();

  // If stopSession is true, then stop the running session
  if(botData.stopSession)
    stopRunningSession();
}

// Handles the motion/driving of the bot. Triggers the engines.
void Bot::handleMotion()
{
  int left = 0;
  int right = 0;
  
  AF_DCMotor _leftMotor(3, MOTOR12_1KHZ);
  AF_DCMotor _rightMotor(4, MOTOR12_1KHZ);

  if(_incomingData.isOnHold || _incomingData.stopSession)
  {
    // Hold position
    
    // Turn off the motors for hold
    _leftMotor.run(RELEASE);
    _rightMotor.run(RELEASE);
    
    _motorRunning = false;
  }
  else if(_incomingData.do360)
  {
    float speed = _incomingData.drivingSpeed;
    
    // Do a 360 with the given speed
    if(speed < 0)
    {
      // Rotate clockwise
      int newSpeed = map(((int)speed * -1), 0, 100, 0, 255);
      
      // Set the speed and the direction for both motors
      _leftMotor.setSpeed(newSpeed);
      _leftMotor.run(FORWARD);
      
      _rightMotor.setSpeed(newSpeed);
      _rightMotor.run(BACKWARD);
    }
    else
    {
      // Rotate counterclockwise 
      int newSpeed = map(((int)speed), 0, 100, 0, 255);
      
      // Set the speed and the direction for both motors
      _leftMotor.setSpeed(newSpeed);
      _leftMotor.run(BACKWARD);
      
      _rightMotor.setSpeed(newSpeed);
      _rightMotor.run(FORWARD);
    }
    
    // Set the correct motor values
    left = speed * -1;
    right = speed;
    _motorRunning = true;
  }
  else
  {
    float speed = _incomingData.drivingSpeed;
    float direction = _incomingData.drivingDirection;
    
    // Drive with the given motion data
    if(direction < 0)
    {
      // The direction is negative, so we turn left.
      // Max speed on the right wheel and a lower speed (based on the value of the turn) on the left wheel.
      left = (100 - (direction * -1)) * (speed / 100);
      right = speed;
    }
    else
    {
      // The direction is posetive, so we turn right
      // Max speed on the left wheel and a lower speed (based on the value of the turn) on the right wheel.
      right = (100 - direction) * (speed / 100);    
      left = speed;
    }
    
    // Based on the calculations before, trigger the motors
    if(left < 0 && right < 0)
    {
      // Set the speed for both motors
      _leftMotor.setSpeed(map((left * -1), 0, 100, 0, 255));
      _rightMotor.setSpeed(map((right * -1), 0, 100, 0, 255));
      
      // Set the direction for both motors
      _leftMotor.run(BACKWARD);
      _rightMotor.run(BACKWARD);
    }
    else
    {
      // Set the speed for both motors
      _leftMotor.setSpeed(map(left, 0, 100, 0, 255));
      _rightMotor.setSpeed(map(right, 0, 100, 0, 255));
      
      // Set the direction for both motors
      _leftMotor.run(FORWARD);
      _rightMotor.run(FORWARD);
    }
    
    _motorRunning = true;
  }
  
  _outgoingData.leftWheel = left;
  _outgoingData.rightWheel = right;
  _outgoingData.inclination = 25.5;  // SET A TEMPORARY STATIC INCLINATION
}

// Retrieves the battery voltage (in percentages of the total (total = 9v))
int Bot::getBatteryVoltage()
{
  float averageTotal = 0;
  byte averageCounter = 0;

  for(int i = 0; i < VOLTAGE_AVERAGE_SPREAD; i++)
  {
    // Read the analog signal and map the value from 0 to 9v
    //  analogRead(_batteryVoltagePort);
    float batteryVoltage = mapFloat(analogRead(0), 0, 1023, 0, 9);
    
    // For the operating of the Arduino we need at least 5v. So lets make 5.5v = 0%;
    // Comparing the measurement from the analog port to the measurement on a multimeter
    // the analog measurement had a small offset of 0.06 volt. Substract that difference.
    batteryVoltage -= (5.5 - 0.06);
    
    // We can only get a accurate measurement when the battery has a load (engines on).
    // When there is no load (engines off), we substract ...v to return an indication that
    // is at least a littile bit better.
    if(!_motorRunning)
      batteryVoltage -= 0.5;
    
    // Add the calculated value to the averageTotal
    averageTotal += batteryVoltage;
  }
  
  // Get the average value based on the average total and map
  // the result to a percentage value. Return the result.
  return mapFloat((averageTotal / VOLTAGE_AVERAGE_SPREAD), 0, 3, 0, 100);
}

// Handles the stopping of the current session
void Bot::stopRunningSession()
{
  
}

float Bot::mapFloat(float x, float in_min, float in_max, float out_min, float out_max)
{
  return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
}
