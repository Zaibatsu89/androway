/*
  Connection.h - Library for handling the XBee connection
*/

#ifndef Connection_h
#define Connection_h

#include <WProgram.h>

#define MESSAGE_INTERVAL 10     // The interval for the message processing in ms
#define SEND_DATA_LENGTH 19     // Length of the data array to send (+1 for the hash)
#define RECEIVE_DATA_LENGTH 8   // Length of the received data array
#define XBEE_ADDRESS 0x01       // Address of the remote XBee
#define BAUDRATE 9600           // Baud rate of the connection
#define HASH_KEY 618674622      // Unique hash key

class Connection
{
  public:
    Connection();                                       // The class constructor
    void Init(XBee xbee);                               // Initializes the XBee connection
    void Send(uint8_t* data, int dataSize);             // Sends the data with xbee
    boolean Receive();                                  // Handles the receiving of the xbee data
    uint8_t* encodeData(uint8_t* data);                 // Encodes the given data array (pointer)
    uint8_t* decodeData(uint8_t* data);                 // Decodes the given data array (pointer), and checks the hash
    uint8_t hash(uint8_t* data, short len);             // Hashes the given data array (pointer)
    uint8_t* getDataIncoming(){return _dataIncoming;};  // Gets the incoming data array (pointer) 
    boolean getHashCorrect(){return _hashCorrect;};     // Gets the hash correct boolean
    void setHashCorrect(boolean hashCorrect){_hashCorrect = hashCorrect;};  // Gets the hash correct boolean
    
  
  private:
    XBee _xbee;
    TxStatusResponse txStatus;                          // Variable that wil contain our response status
    uint8_t dataOutgoing[SEND_DATA_LENGTH];             // Array that will hold the data to send
    uint8_t* _dataIncoming;                             // Array that will contain our recieved data
    boolean _hashCorrect;                               // Boolean for monitoring if the hash is correct
};
    
#endif
