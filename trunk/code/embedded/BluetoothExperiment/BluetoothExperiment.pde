#include <NewSoftSerial.h>
#include "Connection.h"

#define BT_BAUDRATE 9600
#define PIN_BT_DCD 1
#define PIN_BT_TX 2
#define PIN_BT_RX 3
#define PIN_BT_RST 4
#define PIN_LED 13

// Set up a new software serial port
NewSoftSerial btSerial(PIN_BT_RX, PIN_BT_TX);

// Create a new (bt) connection with the desired settings
Connection connection = Connection(BT_BAUDRATE, PIN_BT_RX, PIN_BT_TX, PIN_BT_RST, PIN_BT_DCD, PIN_LED);

void setup()
{
  pinMode(PIN_LED, OUTPUT);
  // Open (start) the serial port
  btSerial.begin(BT_BAUDRATE);
  
  Serial.begin(9600);
  
  // Initialize the (bt) connection, passing the created SoftwareSerial and the desired device name
  connection.init(btSerial, "Androway");
  
  // Attach an interrupt to the RX pin of the bluetooth module
  // Interrupt 0 = pin 2
  // Interrupt 1 = pin 3. Used for checking when there is new data.
  attachInterrupt(1, readData, RISING);
}

// The main program loop
void loop()
{
  // Trigger the loop for the connection
  connection.loop(btSerial);
}

void readData()
{
  connection.receiveData(btSerial);
}
