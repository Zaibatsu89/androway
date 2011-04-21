#include <NewSoftSerial.h>
#include "Connection.h"

#define BT_BAUDRATE 9600
#define PIN_BT_RX 4
#define PIN_BT_TX 3
#define PIN_BT_RST 5

#define ledPin 13

// Set up a new software serial port
NewSoftSerial btSerial(PIN_BT_RX, PIN_BT_TX);

// Create a new (bt) connection with the desired settings
Connection connection = Connection(BT_BAUDRATE, PIN_BT_RX, PIN_BT_TX, PIN_BT_RST);

void setup()
{
  pinMode(ledPin,OUTPUT);
  digitalWrite(ledPin, LOW);
  
  Serial.begin(9600);
  
  // Initialize the (bt) connection, passing the created SoftwareSerial and the desired device name
  connection.init(btSerial, "Androway");
}

void loop()
{
}
