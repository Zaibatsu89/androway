#ifndef Bluetooth_h
#define Bluetooth_h

#include <SoftwareSerial.h>
#include <WProgram.h>
#include <inttypes.h>

class SoftwareSerial;

class Bluetooth
{
   private:
      uint8_t _rxPin;
      uint8_t _txPin;	  
      uint8_t _rstPin;
      uint8_t _dcdPin;
	  static uint8_t _available;
	  //SoftwareSerial &_mySerial;
   public:
      Bluetooth(SoftwareSerial mySerial, int rxPin, int txPin, int rstPint, int dcdPin);
      void begin(SoftwareSerial mySerial, const char data[]);
	  void sendString(SoftwareSerial mySerial, const char s[]);
	  void debugString(SoftwareSerial mySerial, const char s[]);
	  void state();
      int avail();
	  int recByte();
      int recString(char buf[]);
	  void checkString(char buf[]);
      boolean isConnected();
      void send(SoftwareSerial mySerial, char data);
      void send(SoftwareSerial mySerial, char data[]);
      void clearBuf(SoftwareSerial mySerial);
      char read();
      boolean bufEmpty();
};

#endif

