#include <NewSoftSerial.h>
#include <FloatToString.h>
#include <AFMotor.h>
#include "Bluetooth.h"
#include "Bot.h"

#define PIN_BT_DCD 1
#define PIN_BT_TX 2
#define PIN_BT_RX 3
#define PIN_BT_RST 9
#define PIN_LED 13

#define LEFT_MOTOR_NR 3
#define RIGHT_MOTOR_NR 4

// Create the bluetooth object by passing the desired settings, the created NewSoftSerial instance and the needed pin numbers
Bluetooth bluetooth(PIN_BT_RX, PIN_BT_TX, PIN_BT_RST, PIN_BT_DCD, PIN_LED);
Bot bot = Bot(LEFT_MOTOR_NR, RIGHT_MOTOR_NR);

void setup()
{
  Serial.begin(9600);  // Start the serial connection (used for debugging purposes)
  
  // Changing the baudrate doesn't work yet
  bluetooth.begin(9600, "Androway", "4321");              // Start the bluetooth connection
  bluetooth.attachReceiveCallback(handleMessages);        // Attach handleMessages as callback function. Will be triggered when a data message is received.
  bluetooth.attachSendCallback(sendOutgoingData, 1000);   // Attach sendOutgoingData as callback function. Will be triggered when it is time to send a new data message.
  
  // Attach an interrupt to the RX pin of the bluetooth module
  // Interrupt 0 = pin 2
  // Interrupt 1 = pin 3. Used for checking when there is new data.
  attachInterrupt(1, bluetoothInterrupt, RISING);
  
  Serial.println("-setup done-");
}

// The main program loop
void loop()
{
  // Trigger the loop function of the bluetooth module
  bluetooth.loop();
}

// The function is triggered by the interrupt handler.
void bluetoothInterrupt()
{
  // Trigger the receive data function of the bluetooth handler
  bluetooth.receiveData();
}

// The callback funtion for the bluetooth data. Used for processing the data messages.
// This function separates the message into different values and stores the values in a BluetoothData object,
// the bluetooth object is then passed to the setData function of the actual Bot object.
void handleMessages(char data[])
{
  IncomingData botData = {};
  
  const int valueLength = 7;
  char value[valueLength] = "";
  int valueCounter = 0;
  int numberOfValues = 0;
  boolean dataLooped = false;
  
  for(int i = 0; i < RECEIVED_DATA_LENGTH; i++)
  {
    if(!dataLooped)
    {
      if(data[i] == ' ' || data[i] == VALUE_SEPARATOR)
      {
        // We hit a blank value, so we're done reading the data        
        boolean valueIsDigit = true;
        boolean valueIsFloat = false;
        
        // Check the type of the value
        for(int j = 0; j < valueLength; j++)
        {
          // Check if the character is a digit or not
          if(!isDigit(value[j]))
          {
            if(value[j] == '.')              
              valueIsFloat = true;
            else if(value[j] != ' ' && value[j] != '-')
              valueIsDigit = false;
          }
        }
        
        float floatVal = 0;
        int intVal = 0;
        
        if(valueIsDigit && valueIsFloat)
          floatVal = atof(value);  // The value is a float
        else if(valueIsDigit)
          intVal = atoi(value);    // The value is a digit, so short, int or long
        // else                    // The value is a string        
        
        // Store the value in the BluetoothData object, in the correct variable (based on position)
        switch(numberOfValues)
        {
          case 0:
            botData.drivingDirection = floatVal;
            break;
          case 1:
            botData.drivingSpeed = floatVal;
            break;
          case 2:
            botData.isOnHold = intVal;
            break;
          case 3:
            botData.stopSession = intVal;
            break;
        }
        
        // Handle the ending of this value and possibly the ending of the whole message
        if(data[i] == ' ' || i == RECEIVED_DATA_LENGTH - 1)
        {
          // We hit a blank value or we're in the last loop, which means we've reached the end of the data (message is done)
          
          // Set the data as the new data for the bot
          bot.setIncomingData(botData);
          
          dataLooped = true;
          break;
        }
        else
        {
          // We hit a value separator, which means the value is done. So reset for the next value.          
          // Reset/clear the variables that keep track of the value
          memset(value, 0, sizeof(value));          
          char value[valueLength] = "";
          
          valueCounter = 0;
          dataLooped = false;
        }
        
        numberOfValues ++;
      }
      else
      {
        // Add the character to the value array
        value[valueCounter] = data[i];
        valueCounter ++;
      }
    }
  }
}

void sendOutgoingData(char empty[])
{
  // Get the outgoing data
  OutgoingData outgoingData = bot.getOutgoingData();
  
  // Assemble a message to send back
  /*
  String message = VERIFICATION_STRING;
  message += outgoingData.leftWheel;
  message += VALUE_SEPARATOR;
  message += outgoingData.rightWheel;
  message += VALUE_SEPARATOR;
  message += 0; //bluetooth.floatToString(outgoingData.inclination);
  message += MESSAGE_SEPARATOR;
  */
  
  char message[50];
  sprintf(message, "%0s%d%c%d%c%0d.%d%c", VERIFICATION_STRING, outgoingData.leftWheel, VALUE_SEPARATOR, outgoingData.rightWheel, VALUE_SEPARATOR, int(outgoingData.inclination), (int)((outgoingData.inclination - (int)outgoingData.inclination) * 100), MESSAGE_SEPARATOR);
  
  // Send the assembled message
  bluetooth.sendData(message);
}
