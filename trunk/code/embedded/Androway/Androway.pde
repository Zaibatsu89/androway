/*
  Androway.pde - The main controlling unit of the Segway.
*/

//#include <XBee.h>
#include "Connection.h"
#include "Bot.h"

Connection connection = Connection();  // Initialize the Connection
//XBee xbee = XBee();                    // Initialize the XBee

//uint8_t dataOutgoing[SEND_DATA_LENGTH];// The array that will hold the data to send
//uint8_t* dataIncoming = 0;             // The array that will hold the recieved data
int timeout = 100;
unsigned long start;

// Setup of the program
void setup()
{
  Serial.begin(BAUDRATE);
  //xbee.begin(BAUDRATE);   // Open the serial connection with the xbee and set the baudrate
  connection.Init(xbee);  // Initialize the connection and pass along the xbee instance
  start = millis();       // Save current start time for the timer
}

// The main program loop
void loop()
{
  if(int((millis() - start)) >= timeout)
  {
    // Do balance stuff?
    start = millis();
  }
  else
  {
/*    if(connection.Receive() && connection.getHashCorrect())                                // Receive data, check if decode was succesfull and if the hash is valid
    {
      connection.Send(connection.encodeData(gatherData(dataOutgoing)), SEND_DATA_LENGTH);  // We've recieved a correct data message, so now it's our turn to send
      
      dataIncoming = connection.getDataIncoming();                                         // We've recieved data, so fetch it      
      processRecieved(dataIncoming);                                                       // Process the just fetched data
    }
*/
  }
}

// Gathers the needed data (to send) and adds the data to the data array
uint8_t* gatherData(uint8_t* data)
{
  data[0] = bot.getSailingState();
  data[1] = bot.getKeelPosition();
  data[2] = bot.getRudderPosition();
  data[3] = bot.getSailPosition();
  data[4] = bot.getBatteryVoltage();
  data[5] = bot.getInclinationAngle();
  data[6] = bot.getInclinationAngleOverflow();
  data[7] = bot.getWindDirection();
  data[8] = bot.getWindDirectionOverflow();
  data[9] = bot.getEngineState();
  data[10] = bot.getEngineDirection();
  
  return data;
}

// Processes the recieved data
void processRecieved(uint8_t* data)
{
  // Trigger the different functions and pass along the corresponding data
  bot.setSailingState(data[0]);            // Set the sailing state  (automat/manual)
  bot.setKeelPosition((short)data[1]);     // Set the keel position  (up/middle/down)
  bot.setRudderPosition((short)data[2]);     // Set the rudder position
  bot.setSailPosition((short)data[3]);     // Set the sail position
  bot.setEngineState((short)data[4]);      // Set the engine state (on/off)
  bot.setEngineDirection((short)data[5]);  // Set the engine direction
  
  dataIncoming = 0;  // After processing the dataIncoming, set it to zero
}
