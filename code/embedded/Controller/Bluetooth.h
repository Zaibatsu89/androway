/*
  Bluetooth.h - Class for handeling the bluetooth connection and the messages.
 */

#ifndef Bluetooth_h
#define Bluetooth_h

#include <NewSoftSerial.h>
#include <WProgram.h>

#define BUFFER_LENGTH 100                   // The length of the receive buffer
#define RECEIVED_DATA_LENGTH 30             // The length of the data array to receive
#define MAX_MESSAGE_RECEIVE_TIME 10000      // We should receive a message within this time. If it takes longer that this maximum, something went wrong.

#define MESSAGE_SEPARATOR '~'               // The separator used between the different messages
#define VALUE_SEPARATOR ','                 // The separator used between different values in each message
#define VERIFICATION_STRING "$ANDROWAY001"  // The verification string used for verifying the incoming messages

// Check if the given value is a digit (number)
#define isDigit(n) (n >= '0' && n <= '9')

extern "C"
{
  // Define a type for the callback function. Takes a char array as parameter
  typedef void (*BluetoothCallback)(char data[]);
}

class Bluetooth
{
  private:
    NewSoftSerial _mySerial;
    BluetoothCallback _receiveCallback;
    BluetoothCallback _sendCallback;
    int _rxPin;
    int _txPin;
    int _rstPin;
    int _dcdPin;
    int _ledPin;
    int _receiveCounter;
    unsigned long _lastReceivedMessage;
    unsigned long _lastSentMessage;
    unsigned long _sendMessageInterval;
    char _receiveBuffer[BUFFER_LENGTH];
    char _receivedData[RECEIVED_DATA_LENGTH];
      
  public:
    // The constructor for the class, store the pin numbers and initialize the bluetooth software serial connection
    Bluetooth(int rxPin, int txPin, int rstPin, int dcdPin, int ledPin) : _mySerial(rxPin, txPin)
    {
      // Store the given settings in the private variables
      _receiveCallback = NULL;
      _sendCallback = NULL;
      _rxPin = rxPin;
      _txPin = txPin;
      _rstPin = rstPin;
      _dcdPin = dcdPin;
      _ledPin = ledPin;
      
      // Initialize some variables that are used by the class
      _receiveCounter = 0;
      _lastReceivedMessage = -1;
      _lastSentMessage = -1;
      _receiveBuffer[BUFFER_LENGTH] = *"";
      _receivedData[RECEIVED_DATA_LENGTH] = *"";
      _sendMessageInterval = 1500;  // The default interval for sending a message back (in ms)
    };
    
    void begin(long baudrate, char name[], char password[]);  // Function for starting the bluetooth class and connection/module
    void loop();                                              // The main loop for the bluetooth class
    void attachReceiveCallback(BluetoothCallback callback);   // Attach the given function as callback function for received data messages
    void attachSendCallback(BluetoothCallback callback, unsigned long interval);  // Attach the given function as callback function for when it is time to send a new message
    void clearBuffer();                                       // Function that clears the receive buffer
    void receiveData();                                       // Function that handles the receiving of the bluetooth data
    void sendData(char* message);                             // Function for sending data messages to the connected device
    void handleChar(char value);                              // Handle the received character
    void handleMessage();                                     // Handle the received message
    boolean verifyMessage();                                  // Verify the received message based on the verification string
    char* floatToString(float floatVal);                      // Convert the given float to a string
    
    char* appendString(char baseString[], char appendString[]);
};

#endif
