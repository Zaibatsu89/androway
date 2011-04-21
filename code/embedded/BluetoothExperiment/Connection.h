/*
  Connection.h - Library for handling the bluetooth connection
*/

#ifndef Connection_h
#define Connection_h

#include <WProgram.h>

class Connection
{
  public:
    Connection(int baudRate, int rxPin, int txPin, int rstPin);  // The class constructor
    void init(NewSoftSerial mySerial, String deviceName);
    boolean receiveData(NewSoftSerial mySerial);

  private:
    int _baudRate;
    int _rxPin;
    int _txPin;
    int _rstPin;
};
    
#endif
