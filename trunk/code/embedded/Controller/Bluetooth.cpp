/*
  Bluetooth.cpp - Class for handeling the bluetooth connection and the messages.
 */

#include <NewSoftSerial.h>
#include "Bluetooth.h"

// Changing the baudrate doesn't work yet..!!
void Bluetooth::begin(int baudrate, char name[], char password[])
{
  // Start the NewSoftSerial connection with the given baudrate
  _mySerial.begin(baudrate);
  
  pinMode(_rstPin, OUTPUT);
//  pinMode(_dcdPin, INPUT);  // At the moment the DCD pin is not used
  
  // Reset the bluetooth module so that the AT commands can be set to the module
  digitalWrite(_rstPin, LOW);
  delay(2000);
  digitalWrite(_rstPin, HIGH);
  delay(1000);
  
  // The AT commands for configuration of the bluetooth module
  _mySerial.println("ATS10=0");                            // Echo the result of the commands yes (ATS10=1) or no (ATS10=0)
  _mySerial.println(appendString("AT+BTNAME=", name));     // The device name
  _mySerial.println(appendString("AT+BTKEY=", password));  // The bluetooth password  
  _mySerial.println("AT+UARTCONFIG,9600,N,1");             // The baudrate to communicate on
  _mySerial.println("AT+BTMODE,3");
  _mySerial.println("AT+BTSCAN");

  // Clear the bluetooth receive buffer and the actual serial buffer
  clearBuffer();
  _mySerial.flush();
  
  delay(500);
  
  // The initialisation is done, we're nog connected yet. So while waiting for a connection, turn on the led.
  digitalWrite(_ledPin, HIGH);
}

void Bluetooth::loop()
{
  // The time difference between the last received message time and the current time
  long receivedTimeDiff = (millis() - _lastReceivedMessage);

  // If we did not receive any message for the last maxMessageReceiveTime milliseconds, the connection is probably lost so we can go to auto (on hold).
  if((_lastReceivedMessage != -1) && receivedTimeDiff > MAX_MESSAGE_RECEIVE_TIME)
  {
    // The connection was lost?! Handle it properly.
    
    // We have no connection anymore so turn on the led
    digitalWrite(_ledPin, HIGH);
  }
  
  // The time difference between the last sent message time and the current time
  long sentTimeDiff = (millis() - _lastSentMessage);

  // If we did not send any message for the last _sendMessageInterval, send a messa
  if(_sendCallback != NULL && (_lastSentMessage != -1) && sentTimeDiff > _sendMessageInterval)
  {
    // The empty data to send along (seems lame, but now we can use the same BluetoothCallback type as for the receive callback).
    char empty[1] = "";
    
    // Execute the attached callback function
    (*_sendCallback)(empty);
    
    // Store this time as the last time a message was sent.
    _lastSentMessage = millis();
  }
}

// Use the given function as callback function
void Bluetooth::attachReceiveCallback(BluetoothCallback callback)
{
  _receiveCallback = callback;
}

// Use the given function as callback function
void Bluetooth::attachSendCallback(BluetoothCallback callback, unsigned long interval)
{
  _sendMessageInterval = interval;  // The interval for sending a message back (in ms)
  _sendCallback = callback;
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

// Function for sending data messages to the connected device
void Bluetooth::sendData(char* message)
{
  _mySerial.print(message);
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
    
    // If no message was returned yet, set the time in millis so a message will be sent back.
    if(_lastSentMessage == -1)
      _lastSentMessage = millis();
    
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
    if(_receiveCallback != NULL)
      (*_receiveCallback)(_receivedData);
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

char* Bluetooth::floatToString(float floatVal)
{
  char buffer[8];
  int wholeNumber = (floatVal - (int)floatVal) * 100;
  sprintf(buffer, "%0d.%d", (int)floatVal, wholeNumber);
  
  return buffer;
}

// Append the given append string to the base string and return the new string
char* Bluetooth::appendString(char baseString[], char appendString[])
{
  char buffer[40];
  sprintf(buffer, "%0s%s", baseString, appendString);
  
  return buffer;
}
