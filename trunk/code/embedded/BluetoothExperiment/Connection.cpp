#include <NewSoftSerial.h>
#include "Connection.h"

// Class constructor, pass the correct connector pins
Connection::Connection(int baudRate, int rxPin, int txPin, int rstPin)
{  
  // Store the pin numbers and the SoftwareSerial instance in the private variables
  _baudRate = baudRate;
  _rxPin  = rxPin;
  _txPin  = txPin;
  _rstPin = rstPin;
}

void Connection::init(NewSoftSerial mySerial, String deviceName)
{
  // Open the pin communication
  pinMode(_rxPin, INPUT);
  pinMode(_txPin, OUTPUT);
  pinMode(_rstPin, OUTPUT);
  
  // Set the reset pin to high (should be high when running)
  digitalWrite(_rstPin, HIGH);
  
  // Open (start) the serial port
  mySerial.begin(_baudRate);
  
  // Reset the BT module (needed to write our own configurations to the module)
  digitalWrite(_rstPin, LOW);
  delay(2000);
  digitalWrite(_rstPin, HIGH);
  delay(1000);
  
  // Write the new bluetooth configurations to the module
  mySerial.println("ATS10=0");
  delay(200);
  mySerial.println("AT+BTNAME=" + deviceName);
  delay(200);
  mySerial.println("AT+BTMODE,3");
  delay(200);
  mySerial.println("AT+BTSCAN");
  delay(200);
}

boolean Connection::receiveData(NewSoftSerial mySerial)
{
  if(mySerial.available())
  {
    Serial.print(mySerial.read(), BYTE);
  }
}
