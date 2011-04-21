#include "Bluetooth.h"
#include <SoftwareSerial.h>

#define bitDelay 98      // voor 9600 baud  (98/49 of 100/50)
#define halfBitDelay 49
#define bitEndDelay 40

uint8_t Bluetooth::_available = 0; //LOW;

Bluetooth::Bluetooth(SoftwareSerial mySerial, int rxPin, int txPin, int rstPin, int dcdPin)
// Constructor: Bluetooth interface afbeelden op pin rx/tx en rst/dcd
{
   _rxPin  = rxPin;
   _txPin  = txPin;
   _rstPin = rstPin;
   _dcdPin = dcdPin;
   _available = 0; //LOW;
}

void Bluetooth::begin(SoftwareSerial mySerial, const char data[])
{   
   _available = 0; //LOW
   pinMode(_rstPin, OUTPUT);    // reset pin output
   pinMode(_dcdPin, INPUT);     // dcd pin input
   digitalWrite(_dcdPin, 1);    // internal pull up
   digitalWrite(_rstPin, HIGH);
   
   if (data) 
   {
      Serial.print(data);
      Serial.print(" Rx= ");
      Serial.print(_rxPin, DEC);
      Serial.print(" Tx= ");
      Serial.print(_txPin, DEC);
      Serial.print(" RST= ");
      Serial.print(_rstPin, DEC);
      Serial.print(" DCD= ");
      Serial.println(_dcdPin, DEC);
   }
   
   // Reset
   digitalWrite(_rstPin, LOW);
   delay(2000);
   digitalWrite(_rstPin, HIGH);
   delay(1000);
   
   /*
   sendString(mySerial, "ATS10=0");
   delay(200);
   sendString(mySerial, "AT+BTNAME=\"Sibbele\"");
   delay(200);
   sendString(mySerial, "AT+BTMODE,3");
   delay(200);
   sendString(mySerial, "AT+BTSCAN");
   delay(200);
   */
   
   debugString(mySerial, "ats10=1");
   debugString(mySerial, "at+btname=\"Sibbele\"");
   debugString(mySerial, "at+btmode,3");
   debugString(mySerial, "at+btscan");
}

void Bluetooth::sendString(SoftwareSerial mySerial, const char s[])
{
   if (s)
   {
      Serial.println(s);
      mySerial.print(s);   
	  mySerial.print(13, BYTE);
   }
}

void Bluetooth::debugString(SoftwareSerial mySerial, const char s[])
{
   char buffer[12];
   sendString(mySerial, s);
   recString(buffer);
   checkString(buffer);
   delay(100);
} 

void Bluetooth::state()
// Interrupt routine
{
   cli();
   _available = 1; //HIGH;
}

int Bluetooth::avail()
{
   return(_available);
}

int Bluetooth::recByte()
{
  byte val = 0;
  cli();  // even geen interrupts
  while (digitalRead(_rxPin));
  if (digitalRead(_rxPin) == LOW) {
    delayMicroseconds(halfBitDelay);
    for (int offset = 0; offset < 8; offset++) {
       delayMicroseconds(bitDelay);
       val |= digitalRead(_rxPin) << offset;
    }
    delayMicroseconds(bitDelay);         // wait for stop bit + extra
    delayMicroseconds(bitEndDelay);      
  }
  sei();  //  nu mogen interrupts weer
  return val&0x7F;
}

int Bluetooth::recString(char buf[])
// Vul buffer met string; String afsluiten met \0; len is aantal tekens, zonder \0
// Nog doen: timeout
{
   int i = 0;
   char ch = recByte();
   delay(2);
   while (ch != '\n')
   {
      if (ch != '\r') buf[i++] = ch;  // skip <cr>
      ch = recByte();
   }
   buf[i] = '\0';
   return i;
}

void Bluetooth::checkString(char buf[])
{
   int len = strlen(buf);
   Serial.print("["); Serial.print(len, DEC); Serial.print("]");
   Serial.print(buf);
   Serial.print(" -> ");
   if (strcmp("OK", buf)==0) {
      Serial.println("OK");
   } else
   if (strcmp("CONNECT", buf)==0) {
      Serial.println("CONNECT");
   } else
   if (strcmp("DISCONNECT", buf)==0) {
      Serial.println("DISCONNECT");
   } else
   if (strcmp("ERROR", buf)==0) {
      Serial.println("ERROR");
   } else {
      Serial.println("?");
   }
}

boolean Bluetooth::isConnected()
{
  int result = digitalRead(_dcdPin);
  
  Serial.println(result);
  
  return (!result);
}

void Bluetooth::send(SoftwareSerial mySerial, char data)
{
   mySerial.print(data);
}

void Bluetooth::send(SoftwareSerial mySerial, char data[])
{
   mySerial.print(data);
}

void Bluetooth::clearBuf(SoftwareSerial mySerial)
{
  //mySerial.flush();
}

char Bluetooth::read()
{
   return recByte();
}

boolean Bluetooth::bufEmpty()
{
   return (Serial.available() == 0);
}

