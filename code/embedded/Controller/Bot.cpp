/*
  Bot.cpp - Class for interacting with the physical balancing bot.
 */

#include <AFMotor.h>
#include "Bot.h"

// Set the new data for the bot
void Bot::setData(struct BotData botData)
{
  // Store the given BotData as the new data for the bot
  _data = botData;

  // After the new data is set, handle the motion of the bot
  handleMotion();
}

// Handles the motion/driving of the bot. Triggers the engines.
void Bot::handleMotion()
{
  int left = 0;
  int right = 0;
  
  AF_DCMotor _leftMotor(3, MOTOR12_1KHZ);
  AF_DCMotor _rightMotor(4, MOTOR12_1KHZ);

  if(!_data.isOnHold)
  {
    float speed = _data.drivingSpeed;
    float direction = _data.drivingDirection;
    
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
    
    Serial.print("left: "); Serial.print(left);
    Serial.print(", right: "); Serial.print(right);
  }
  else
  {
    // Hold position
    Serial.print("On hold");
    
    // Turn off the motors for hold
    _leftMotor.run(RELEASE);
    _rightMotor.run(RELEASE);
  }
  
  Serial.println();
}
