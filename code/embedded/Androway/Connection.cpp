/*
  Connection.cpp - Library for handling the XBee connection
*/

#include <XBee.h>
#include "Connection.h"

// The class constructor
Connection::Connection()
{
  txStatus = TxStatusResponse();  // Initialize the TxStatusResponse
  _dataIncoming = 0;              // Initialize _dataIncoming to zero
  _hashCorrect = false;           // Initialize _hashCorrect to false
}

// Initializes the connection
void Connection::Init(XBee xbee)
{
  _xbee = xbee;  // Assign the given xbee instance to our local xbee instance
}

// Sends the data with xbee
void Connection::Send(uint8_t* data, int dataSize)
{
  Tx16Request tx = Tx16Request(XBEE_ADDRESS, (uint8_t*)data, dataSize);   // Set the request its correct data and address
  _xbee.send(tx);                                                         // Send the data
}
  
boolean Connection::Receive()
{
  if(_xbee.readPacket(MESSAGE_INTERVAL))
  {
    XBeeResponse response = _xbee.getResponse();             // There is a response, fetch the actual response
    
    if(response.getApiId() == RX_16_RESPONSE)                // Check if the response type is a RX_16_RESPONSE 
    {
      if(response.isAvailable())                             // Check if the response has successfully been parsed, is complete and ready for use. 
      {
        Rx16Response rx16 = Rx16Response();                  // Initialize a Rx16Response
        response.getRx16Response(rx16);                      // Fetch and set the Rx16Response
        _dataIncoming = decodeData(rx16.getData());          // Succesfull response, get and decode the response data
        
        return true;                                         // Return true so that the controller knows that we got a data message
      }
      else
      {
        return false;                                        // Return false so that the controller knows that we did not receive a correct data message
      }
    }
    else if(response.getApiId() == TX_STATUS_RESPONSE)       // Check if the response type is a TX_STATUS_RESPONSE
    {
      response.getZBTxStatusResponse(txStatus);              // Fetch and set the TxStatusResponse
      if (txStatus.getStatus() == SUCCESS)                   // Get the delivery status and check if it was succesfull
      {
                                                             // Succesfull message response
      }
      else
      {
                                                             // Unsuccesfull message response
      }
      return false;                                          // Return false so that the controller knows that we dit not get a data message
    }
    else
    {
      return false;                                          // Message has an unknown status so return false
    }
  }
  else
  {
    return false;                                            // No message received, so return false
  }
}

// Encodes the given data array (pointer)
uint8_t* Connection::encodeData(uint8_t* data)
{
  data[SEND_DATA_LENGTH - 1] = hash(data, SEND_DATA_LENGTH - 1);  // Add the hash to the end of the data array
  
  return data;                            // Returns the encoded data array (pointer)
}

// Decodes the given data array (pointer), and checks the hash
uint8_t* Connection::decodeData(uint8_t* data)
{
  if(hash(data, RECEIVE_DATA_LENGTH - 1) == data[RECEIVE_DATA_LENGTH - 1])  // Check if the hash equals the hash that was sent along
  {
    _hashCorrect = true;                                  // The hash was correct so set _hashCorrect to true
    return data;                                          // Return the data array (pointer) because it was correct data
  }
  else
  {
    _hashCorrect = false;                                 // The hash was incorrect so set _hashCorrect to false
    return 0;
  }
}

// Hashes the given data array (pointer)
uint8_t Connection::hash(uint8_t* data, short len)
{
  int hash = 0;                   // Initialize the hash variable to zero  
  for(short i = 0; i < len; i++)  // Loop the data array
    hash ^= data[i];              // Xor the hash with the data item
  
  hash ^= HASH_KEY;               // After hashing the data, xor the result with the HASH_KEY
  
  while (hash >= 256)             // For sending, while the hash is bigger than 256 derease it with 20
    hash -= 20;
    
  return (uint8_t)hash;           // Return the hash
}
