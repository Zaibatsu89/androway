#ifndef Bluetooth_h
#define Bluetooth_h

#include <NewSoftSerial.h>
#include <WProgram.h>

/**
 * The length of the receive buffer
 */
#define BUFFER_LENGTH 100

/**
 * The length of the data array to receive
 */
#define RECEIVED_DATA_LENGTH 30

/**
 * We should receive a message within this time. If it takes longer that this maximum, something went wrong.
 */
#define MAX_MESSAGE_RECEIVE_TIME 10000

/**
 * The separator used between the different messages
 */
#define MESSAGE_SEPARATOR '~'

/**
 * The separator used between different values in each message
 */
#define VALUE_SEPARATOR ','

/**
 * The verification string used for verifying the incoming messages
 */
#define VERIFICATION_STRING "$ANDROWAY001"

/**
 * A function for checking if the given value is a digit (number)
 */
#define isDigit(n) (n >= '0' && n <= '9')

extern "C"
{
  /**
   * Object used for callback functions.
   * @author Tymen Steur
   * @since 06-06-2011
   * @version 0.3
   * @param The data to pass to the callback function
   */
  typedef void (*BluetoothCallback)(char data[]);
}

/**
 * The Bluetooth class for handeling a bluetooth connection and the sending/receiving of messages.
 * @author Tymen Steur
 * @since 06-06-2011
 * @version 0.3
 */
class Bluetooth
{
  private:
    /**
     * The new soft serial for the serial connection with the bluetooth adaptor
     */
    NewSoftSerial _mySerial;
    
    /**
     * The callback function to trigger when data was received
     */
    BluetoothCallback _receiveCallback;
    
    /**
     * The callback function to trigger when a message has to be sent (based on timer)
     */
    BluetoothCallback _sendCallback;
    
    /**
     * The rx (receive) pin number for the bluetooth adaptor
     */
    byte _rxPin;
    
    /**
     * The tx (transmit) pin number for the bluetooth adaptor
     */
    byte _txPin;
    
    /**
     * The rst (reset) pin number for the bluetooth adaptor
     */
    byte _rstPin;
    
    /**
     * The dcd (data carrier detect) pin number for the bluetooth adaptor
     */
    byte _dcdPin;
    
    /**
     * The pin number for the led
     */
    byte _ledPin;
    
    /**
     * Used for counting the number of characters received
     */
    byte _receiveCounter;
    
    /**
     * Used to hold the time we last received a message in millis
     */
    unsigned long _lastReceivedMessage;
    
    /**
     * Used to hold the time we last sent a message in millis
     */
    unsigned long _lastSentMessage;
    
    /**
     * The interval for sending messages to the remote device
     */
    unsigned long _sendMessageInterval;
    
    /**
     * The buffer for holding the received characters
     */
    char _receiveBuffer[BUFFER_LENGTH];
    
    /**
     * The buffer for holding the received data
     */
    char _receivedData[RECEIVED_DATA_LENGTH];
    
    /**
     * Clear the receive buffer
     */
    void clearBuffer();
    
    /**
     * Handle the received character
     * @param value  The received character
     */
    void handleChar(char value);
    
    /**
     * Handle the received message
     */
    void handleMessage();
    
    /**
     * Verify the received message based on the verification string
     * @returns Wheter the message was correct or not
     */
    boolean verifyMessage();
      
  public:
    /**
     * The constructor for the Bluetooth class. Initializes the (software) serial connection.
     * @param rxPin   The pin number for the rx (receive) pin
     * @param txPin   The pin number for the tx (transmit) pin
     * @param rstPin  The pin number for the rst (reset) pin
     * @param dcdPin  The pin number for the dcd (data carrier detect) pin
     * @param ledPin  The pin number for the led
     */
    Bluetooth(byte rxPin, byte txPin, byte rstPin, byte dcdPin, byte ledPin) : _mySerial(rxPin, txPin)
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
    
    /**
     * Starting the bluetooth connection/module
     * @param baudrate  The baudrate to start the bluetooh connection with (CURRENTLY DOESN'T WORK WITH ANY OTHER VALUE THEN 9600)
     * @param name      The name for the bluetooth device
     * @param password  The pin/password to use for the connection
     */
    void begin(int baudrate, char name[], char password[]);
    
    /**
     * The main loop for the bluetooth class
     */
    void loop();
    
    /**
     * Attaches the given function as callback for received data messages
     * @param callback  The callback function
     */
    void attachReceiveCallback(BluetoothCallback callback);
    
    /**
     * Attaches the given function as callback for when it is time to send a new message
     * @param callback  The callback function
     * @param interval  The time interval for sending messages in millis
     */
    void attachSendCallback(BluetoothCallback callback, unsigned long interval);
    
    /**
     * Handle the receiving of the bluetooth data
     */
    void receiveData();
    
    /**
     * Send the given message to the connected device
     * @param message  The data message to send
     */
    void sendData(char* message);    
    
    /**
     * Convert the given float to a string
     * @param floatVal  The float to convert
     * @return The string containing the float value
     */
    char* floatToString(float floatVal);
    
    /**
     * Append the given append string to the base string and return the new string
     * @param baseString    The base string
     * @param appendString  The string to append
     * @return The merged string
     */
    char* appendString(char baseString[], char appendString[]);
};

#endif
