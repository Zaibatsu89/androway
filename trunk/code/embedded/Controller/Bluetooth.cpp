/*
  Bluetooth.cpp - Class for handeling the bluetooth connection and the messages.
 */

#include <NewSoftSerial.h>
#include "Bluetooth.h"

// Changing the baudrate doesn't work yet..!!
void Bluetooth::begin(long baudrate, String name, String password)
{
  // Start the NewSoftSerial connection with the given baudrate
  _mySerial.begin(baudrate);
  
  pinMode(_rstPin, OUTPUT);
  pinMode(_dcdPin, INPUT);
  
  // Reset the bluetooth module so that the AT commands can be set to the module
  digitalWrite(_rstPin, LOW);
  delay(2000);
  digitalWrite(_rstPin, HIGH);
  delay(1000);
  
  // The AT commands for configuration of the bluetooth module
  _mySerial.println("ATS10=0");                  // Echo the result of the commands yes (ATS10=1) or no (ATS10=0)
  _mySerial.println("AT+BTNAME=" + name);  // The device name  
  _mySerial.println("AT+BTKEY=" + password);     // The bluetooth password  
  _mySerial.println("AT+UARTCONFIG,9600,N,1");   // The baudrate to communicate on
  _mySerial.println("AT+BTMODE,3");
  _mySerial.println("AT+BTSCAN");

  // Clear the bluetooth receive buffer and the actual serial buffer
  clearBuffer();
  _mySerial.flush();
  
  delay(500);
  
  // The initialisation is done, we're nog connected yet. So while waiting for a connection, turn on the led.
  digitalWrite(_ledPin, HIGH);
  Serial.println("-bluetooth setup done-");
}

void Bluetooth::loop()
{
  // For temporary use, if no message was received yet, send a message to the Android device
  if(_lastReceivedMessage == -1)
  {
    _mySerial.print("Waiting...");
    
    delay(4000);
  }
   
  // The time difference between the last received message time and the current time
  long timeDiff = (millis() - _lastReceivedMessage);
  
  // If we did not receive any message for the last maxMessageReceiveTime milliseconds, the connection is probably lost so we can go to auto (on hold).
  if((_lastReceivedMessage != -1) && timeDiff > MAX_MESSAGE_RECEIVE_TIME)
  {
    // Handle connection lost stuff
    Serial.println("There was no message for more then 10 seconds. Was the connection was lost? Currently unhandled.");
    
    // We have no connection anymore so turn on the led
    digitalWrite(_ledPin, HIGH);
    
    delay(2500);
  }
}

// Use the given function as callback function
void Bluetooth::attach(BluetoothCallback callback)
{
  _callback = callback;
}

// Clear the receive buffer
void Bluetooth::clearBuffer()
{
  for(int i = 0; i < BUFFER_LENGTH; i++)
  {
    char value = ' ';
    _receiveBuffer[i] = value;
  }
}

// Function that handles the receiving of the data
void Bluetooth::receiveData()
{
  cli();
  while(_mySerial.available())
  {
    handleChar(_mySerial.read());
  }
  sei();
}

// Handle the received character
void Bluetooth::handleChar(char value)
{
  _receiveBuffer[_receiveCounter] = value;  // Add the byte to the receive buffer
  _receiveCounter ++;
  
  // If the received char equals the separator charactor it means the message is done      
  if(value == MESSAGE_SEPARATOR)
    handleMessage();
}

// Handle the received message
void Bluetooth::handleMessage()
{
  // Check if verification succeeded
  if(verifyMessage())
  {
    // We received a verified message, set the last received message time to the current time and make sure the led is turned off
    _lastReceivedMessage = millis();    
    digitalWrite(_ledPin, LOW);
    
    // The start and the end of the actual data in the complete message array (so without verification and message separator characters)
    short mStart = sizeof(VERIFICATION_STRING) - 1;
    int mEnd = sizeof(_receiveBuffer);
    
    // Put the received data from the buffer, into the data array        
    for(int i = mStart; i < mEnd; i++)
    {
      char value = ' ';
      
      if(i < (_receiveCounter - 1))
        value = _receiveBuffer[i];
      
      _receivedData[i - mStart] = value;
    }
    
    // If a callback function is attached, execute the callback function and pass the _receivedData array.
    // Otherwise print the received data array to the serial monitor (for debugging purposes?!)
    if(_callback != NULL)
      (*_callback)(_receivedData);
    else
    {
      Serial.print(_receivedData);
      Serial.println();
    }
  }
    
  // Reset the receiveCounter and clear the receive buffer
  _receiveCounter = 0;
  clearBuffer();
  _mySerial.flush();
}

// Verify the received message based on the verification string
boolean Bluetooth::verifyMessage()
{
  boolean result = false;
  
  // Verify if the start of the message is correct
  int verified = 0;
  int verificationStringLength = sizeof(VERIFICATION_STRING) - 1;
  
  for(int i = 0; i < verificationStringLength; i++)
  {
    char stringElement = VERIFICATION_STRING[i];
    char str = _receiveBuffer[i];
    
    if(str == stringElement)
      verified ++;
  }
  
  // If the length of the verification matches the length of the verification string, the verification succeeded.
  if(verified == verificationStringLength)
    result = true;
  else
    Serial.println("Message verification failed!");
  
  return result;
}
