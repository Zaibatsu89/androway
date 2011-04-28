/*
  Connection.h - Library for handling the bluetooth connection
*/

#ifndef Connection_h
#define Connection_h

#include <WProgram.h>

#define BUFFER_LENGTH 100              // The length of the receive buffer
#define VERIFICATION_STRING_LENGTH 13  // The length of the verification string
#define RECEIVED_DATA_LENGTH 30        // The length of the data array to receive

class Connection
{
  public:
    // The class constructor
    Connection(int baudRate, int rxPin, int txPin, int rstPin, int dcdPin, int ledPin);
    void init(NewSoftSerial mySerial, String deviceName);
    void loop(NewSoftSerial mySerial);
    void receiveData(NewSoftSerial mySerial);
    void processData();
    void clearBuffer();

  private:
    char* verificationString[VERIFICATION_STRING_LENGTH];  // The verification string for the device. Each message should start with this for it to be valid.
    char messageSeparator;                                // The separator used between different messages
    char valueSeparator;                                        // The separator used between different values in each message
    int maxMessageReceiveTime;                            // We should receive a message within this time. If it takes longer that this maximum, something went wrong.
    
    char* receiveBuffer[BUFFER_LENGTH];
    int receiveCounter;
    long lastReceivedMessage;  // The last time - in millis() - that we received a message from the Android device.
    char* receivedData[RECEIVED_DATA_LENGTH];
    
    uint8_t _baudRate;
    uint8_t _rxPin;
    uint8_t _txPin;
    uint8_t _rstPin;
    uint8_t _dcdPin;
    uint8_t _ledPin;
};
    
#endif
