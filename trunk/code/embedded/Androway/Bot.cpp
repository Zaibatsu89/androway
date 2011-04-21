/*
  Bot.cpp - Class for interacting with the physical balancing bot.
 */

#include "WProgram.h"
#include "Bot.h"

// Constructor for the bot class
Bot::Bot()
{
  _dataElm1 = 0;  // Future data elements
  _dataElm2 = 0;  // Future data elements
  _dataElm3 = 0;  // Future data elements
}

// Set the sailing state
void Bot::setDataElm1(uint8_t dataElm)
{
  _dataElm1 = dataElm;  // Save the given data element to the main variable
}
