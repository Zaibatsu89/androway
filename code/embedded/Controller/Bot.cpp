/*
  Bot.cpp - Class for interacting with the physical balancing bot.
 */

#include "WProgram.h"
#include "Bot.h"

// Set the new data for the bot
void Bot::setData(struct BotData botData)
{
  // Store the given BotData as the new data for the bot
  _data = botData;

  Serial.println("Set data:");
  Serial.print(_data.drivingDirection);
  Serial.print(", ");
  Serial.print(_data.drivingSpeed);
  Serial.print(", ");
  Serial.print(_data.isOnHold, DEC);
  Serial.println();
};

