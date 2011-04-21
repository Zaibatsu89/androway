/*
  Controller.pde - The main controlling unit of the boat.
*/

#include <Servo.h>
#include <Wire.h>
#include <XBee.h>
#include "Connection.h"
#include "Boat.h"

Connection connection = Connection();  // Initialize the Connection
XBee xbee = XBee();                    // Initialize the XBee
Boat boat = Boat();                    // Initialize the Boat

uint8_t dataOutgoing[SEND_DATA_LENGTH];// The array that will hold the data to send
uint8_t* dataIncoming = 0;             // The array that will hold the recieved data
int timeout = 100;
unsigned long start;

// Setup of the program
void setup()
{
  Serial.begin(BAUDRATE);
  xbee.begin(BAUDRATE);   // Open the serial connection with the xbee and set the baudrate
  connection.Init(xbee);  // Initialize the connection and pass along the xbee instance
  Wire.begin();           // Connects I2C for the compass  
  start = millis();       // Save current start time for the timer
}

// The main program loop
void loop()
{  
/*
  if(boat.getRfid() == 0)
  {
    boat.readRfid();
    delay(200);
  }
  else
*/
  if(int((millis() - start)) >= timeout)
  {
    boat.checkKeelPosition();    // Check the position of the keel transmission engine
    boat.checkSailPosition();    // Check the position of the sail position transmission engine (also handles the sail automat)
    start = millis();
  }
  else
  {
    if(connection.Receive() && connection.getHashCorrect())                                // Receive data, check if decode was succesfull and if the hash is valid
    {
      connection.Send(connection.encodeData(gatherData(dataOutgoing)), SEND_DATA_LENGTH);  // We've recieved a correct data message, so now it's our turn to send
      
      dataIncoming = connection.getDataIncoming();                                         // We've recieved data, so fetch it      
      processRecieved(dataIncoming);                                                       // Process the just fetched data
    }
  }
}

// Gathers the needed data (to send) and adds the data to the data array
uint8_t* gatherData(uint8_t* data)
{
    data[0] = boat.getSailingState();
    data[1] = boat.getKeelPosition();
    data[2] = boat.getRudderPosition();
    data[3] = boat.getSailPosition();
    data[4] = boat.getBatteryVoltage();
    data[5] = boat.getInclinationAngle();
    data[6] = boat.getInclinationAngleOverflow();
    data[7] = boat.getWindDirection();
    data[8] = boat.getWindDirectionOverflow();
    data[9] = boat.getEngineState();
    data[10] = boat.getEngineDirection();
  data[11] = 0;//boat.getHeading();
  data[12] = 0;//boat.getHeadingOverflow();
  data[13] = 33;//boat.getTemperatureEnvironment();
  data[14] = 1;//boat.getTemperatureEnvironmentOverflow();
  data[15] = 25;//boat.getTemperatureWater();
  data[16] = 0;//boat.getTemperatureWaterOverflow();
  data[17] = 1;//boat.getRfid();
  
  return data;
}

// Processes the recieved data
void processRecieved(uint8_t* data)
{
  // Trigger the different functions and pass along the corresponding data
  boat.setSailingState(data[0]);            // Set the sailing state  (automat/manual)
  boat.setKeelPosition((short)data[1]);     // Set the keel position  (up/middle/down)
  boat.setRudderPosition((short)data[2]);     // Set the rudder position
  boat.setSailPosition((short)data[3]);     // Set the sail position
  boat.setEngineState((short)data[4]);      // Set the engine state (on/off)
  boat.setEngineDirection((short)data[5]);  // Set the engine direction

  if((short)data[6] == 1)
    boat.emergencyStop();    // If the emergency data element is set to one, trigger the emergency stop
  
  dataIncoming = 0;  // After processing the dataIncoming, set it to zero
}
