/*
 * Bluetooth
 * by Sibbele Oosterhaven
 * Demonstrates communication with a bluetooth device
 * servo control via a analog input
*/
#include <ctype.h>
#include <SoftwareSerial.h>
#include "Bluetooth.h"

SoftwareSerial mySerial(4, 3);            // rx, tx
Bluetooth bt(mySerial, 4, 3, 5, 2);       // rx, tx, rst, dcd

int ledPin = 13;

volatile int serialavailable = LOW;

void setup()
{
  pinMode(ledPin, OUTPUT);
  
  Serial.begin(9600);
  Serial.println("Bluetooth test");
   
  mySerial.begin(9600);  // soft serial
  bt.begin(mySerial, "Init Bluetooth");    //laptop  001BFC1253A1  //RC10346-8A12716
   
  // While not connected, blink the led 
  while(!bt.isConnected())
  {
     digitalWrite(ledPin, HIGH);
     delay(200);
     digitalWrite(ledPin, LOW);
     delay(200);
  }
   
  Serial.println("Bluetooth connected");

  // When connected stop the led blinking
  digitalWrite(ledPin, LOW);
  attachInterrupt(0, serialstate, FALLING);  // soft rx triggering interrupt
}

// Interrupt routine
void serialstate()
{
   cli();
   serialavailable = HIGH;
   Serial.println("Available");
}

void loop()
{
  
}
