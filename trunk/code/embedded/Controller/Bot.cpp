/*
  Bot.cpp - Class for interacting with the physical balancing bot.
 */

#include <AFMotor.h>
#include "Bot.h"

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
  
  AF_DCMotor _leftMotor(_leftMotorNr, MOTOR12_1KHZ);
  AF_DCMotor _rightMotor(_rightMotorNr, MOTOR12_1KHZ);

  if(!_incomingData.isOnHold && !_incomingData.stopSession)
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
  }
  else
  {
    // Hold position
    Serial.print("On hold");
    
    // Turn off the motors for hold
    _leftMotor.run(RELEASE);
    _rightMotor.run(RELEASE);
  }
  
  _outgoingData.leftWheel = left;
  _outgoingData.rightWheel = right;
  _outgoingData.inclination = 25.5;  // SET A TEMPORARY STATIC INCLINATION
}

// Handles the stopping of the current session
void Bot::stopRunningSession()
{
  
}
