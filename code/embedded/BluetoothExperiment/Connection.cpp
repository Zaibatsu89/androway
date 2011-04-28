#include <NewSoftSerial.h>
#include "Connection.h"

// Class constructor, pass the correct connector pins
Connection::Connection(int baudRate, int rxPin, int txPin, int rstPin, int dcdPin, int ledPin)
{
  // Store the pin numbers and the SoftwareSerial instance in the private variables
  _baudRate = baudRate;
  _rxPin  = rxPin;
  _txPin  = txPin;
  _rstPin = rstPin;
  _dcdPin = dcdPin;
  _ledPin = ledPin;
  
  verificationString[VERIFICATION_STRING_LENGTH] = "$ANDROWAY001";  // The verification string for the device. Each message should start with this for it to be valid.
  messageSeparator = '~';               // The separator used between different messages
  valueSeparator = ',';            // The separator used between different values in each message
  maxMessageReceiveTime = 10000;        // We should receive a message within this time. If it takes longer that this maximum, something went wrong.
  
  receiveBuffer[BUFFER_LENGTH] = "";
  receiveCounter = 0;
  lastReceivedMessage = -1;
  receivedData[RECEIVED_DATA_LENGTH] = "";
}

void Connection::init(NewSoftSerial mySerial, String deviceName)
{
  // Open the pin communication
  pinMode(_rxPin, INPUT);
  pinMode(_dcdPin, INPUT);
  pinMode(_txPin, OUTPUT);
  pinMode(_rstPin, OUTPUT);
  
  // Set the reset pin to high (should be high when running)
  digitalWrite(_rstPin, HIGH);
  
  // Open (start) the serial port
//  mySerial.begin(_baudRate);
  
  // Reset the BT module (needed to write our own AT config commands to the module)
  digitalWrite(_rstPin, LOW);
  delay(2000);
  digitalWrite(_rstPin, HIGH);
  delay(1000);
  
  // Write the new bluetooth configurations to the module
  mySerial.println("ATS10=0");                  // Echo the result of the commands yes (ATS10=1) or no (ATS10=0)
  mySerial.println("AT+BTNAME=" + deviceName);  // The device name
  mySerial.println("AT+BTMODE,3");
  mySerial.println("AT+BTSCAN");
  
  // Clear the mySerial receive buffer
  clearBuffer();
}

// The main loop for the bluetooth connection
void Connection::loop(NewSoftSerial mySerial)
{
  // For temporary use, if no message was received yet, send a message to the Android device
  if(lastReceivedMessage == -1)
  {
    mySerial.print("Waiting...");
    
    delay(4000);
  }
  
  // If we did not receive any message for the last maxMessageReceiveTime milliseconds, the connection is probably lost so we can go to auto (hold).
  if((lastReceivedMessage != -1) && ((millis() - lastReceivedMessage) > maxMessageReceiveTime))
  {
    // Handle connection lost stuff
    Serial.println("There was no message for 10 seconds. Was the connection was lost? Currently unhandled.");
    
    // We have no connection anymore so turn on the led
    digitalWrite(_ledPin, HIGH);
    
    delay(1500);
  }
}

// Function that handles the receiving of the bluetooth data
void Connection::receiveData(NewSoftSerial mySerial)
{
  if(mySerial.available())
  {
    char receivedChar = mySerial.read();  // Read a byte from the mySerial port
    
    receiveBuffer[receiveCounter] = &receivedChar;  // Add the byte to the receive buffer
    receiveCounter ++;
    
    // If the received char equals the separator charactor it means the message is done    
    if(receivedChar == messageSeparator)
    {
      // Verify if the start of the message is correct
      short verified = 1;
      for(int i = 0; i < VERIFICATION_STRING_LENGTH; i++)
      {
        if(receiveBuffer[i] == verificationString[i])
          verified ++;
      }
      
      // Check if verification succeeded
      if(verified == VERIFICATION_STRING_LENGTH)
      {
        // We received a verified message, set the last received message time to the current time
        lastReceivedMessage = millis();
        
        // The start and the end of the actual data in the complete message array (so without verification and message separator characters)
        short mStart = VERIFICATION_STRING_LENGTH - 1;
        short mEnd = mStart + RECEIVED_DATA_LENGTH;
        
        // Put the received data from the buffer, into the data array        
        for(int i = mStart; i < mEnd; i++)
        {
          char blank = ' ';
          char* value = &blank;
          
          if(i < (receiveCounter - 1))
            value = receiveBuffer[i];
          
          receivedData[i - mStart] = value;
        }
        
        // Process the received data
        processData();
      }
      
      // Reset the receiveCounter and clear the receive buffer
      receiveCounter = 0;
      clearBuffer();
    }
  }
}

// Process the received data
void Connection::processData()
{
  for(int i = 0; i < RECEIVED_DATA_LENGTH; i++)
  {
    Serial.print(receivedData[i]);
  }
  
  Serial.println();
}

// Clear the receive buffer
void Connection::clearBuffer()
{
  for(int i = 0; i < BUFFER_LENGTH; i++)
  {
    char value = ' ';
    receiveBuffer[i] = &value;
  }
}
