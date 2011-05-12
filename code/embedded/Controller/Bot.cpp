/*
  Bot.cpp - Class for interacting with the physical balancing bot.
 */

#include "Bot.h"

// Set the new data for the bot
void Bot::setData(struct BotData botData)
{
  // Store the given BotData as the new data for the bot
  _data = botData;

  handleMotion();
}

// Handles the motion/driving of the bot. Triggers the engines.
void Bot::handleMotion()
{
  int left = 0;
  int right = 0;

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
  }
  else
  {
    // Hold position
    Serial.print("On hold! - ");
  }
  
  Serial.print("left: "); Serial.print(left);
  Serial.print(", right: "); Serial.print(right);
  Serial.println();
}
