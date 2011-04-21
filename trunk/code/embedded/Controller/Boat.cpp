/*
  Boat.cpp - Class for interacting with the physical boat.
 */

#include "WProgram.h"
#include "Boat.h"
#include <Servo.h>
#include <Wire.h>

// Constructor for the boat class
Boat::Boat()
{
  pinMode(RFID_OUTPUT_PIN, OUTPUT);    // Set digital pin 2 as OUTPUT to connect it to the RFID /ENABLE pin 
  digitalWrite(RFID_OUTPUT_PIN, LOW);  // Activate the RFID reader
  
  _headingOverflow = 0;           // Heading overflow byte
  _tempEnvironmentOverflow = 0;   // Environment temperature overflow byte
  _tempWaterOverflow = 0;         // Water temperature overflow byte
  _windDirectionOverflow = 0;     // Wind direction overflow byte
  _inclinationAngleOverflow = 0;  // Inclination overflow byte
  
  _engineState = 0;               // Init the engine state to zero
  _engineDirection = 0;           // Init the engine direction to zero
  _keelPosition = 0;              // Init the keel position to zero
  _keelValue = 0;                 // Init the keel value to zero
  _rudderPosition = 90;           // Init the rudder position to 90
  _sailPosition = 0;              // Init the sail position to zero
  _sailingState = 0;              // Init the sailing state to zero
  
  _rfidState = 0;                 // Init the RFID state to zero
  _rfidTag = "0415ED3897";        // Init our RFID master tag
}

// Temporary function to directly set the keel position ( for testing purposes )
void Boat::tempKeelPosition(int position)
{
  pinMode(SLACKING_KEEL_PIN, OUTPUT);
  pinMode(STRAINING_KEEL_PIN, OUTPUT);
  
  short val = analogRead(POT_MEASURER_KEEL_PIN);    // Read the input pin
    
  if (val < (position - 10))
  {
    digitalWrite(SLACKING_KEEL_PIN, HIGH);
    digitalWrite(STRAINING_KEEL_PIN, LOW);
  }
  else if (val > (position + 10))
  {
    digitalWrite(SLACKING_KEEL_PIN, LOW);
    digitalWrite(STRAINING_KEEL_PIN, HIGH);
  }
  else
  {
    digitalWrite(SLACKING_KEEL_PIN, LOW);
    digitalWrite(STRAINING_KEEL_PIN, LOW);    
  }
}

// Set the sailing state
void Boat::setSailingState(uint8_t sailingState)
{
  _sailingState = sailingState;  // Save the given sailing state to the main variable
}

// Set the keel position
void Boat::setKeelPosition(short keelPosition)
{
  _keelPosition = keelPosition;  // Save the given keel position to the main variable
  short waardekiel = 0;          // Init the keel value to zero
  
  switch(keelPosition)
  {
    case 0:
      waardekiel = 350;
      break;
    case 1:
      waardekiel = 250;          // If the keel position is 1, set the keel to this position
      break;
    case 2:
      waardekiel = 200;          // If the keel position is 2, set the keel to this position
      break;
  }
  
  _keelValue = waardekiel;       // Save the new keel value to the main variable
  
  checkKeelPosition();           // Check/trigger the keel position
}

void Boat::checkKeelPosition()
{
  // Open the pin communication
  pinMode(SLACKING_KEEL_PIN, OUTPUT);
  pinMode(STRAINING_KEEL_PIN, OUTPUT);
  
  short val = analogRead(POT_MEASURER_KEEL_PIN);  // Read the input pin
  
  // Check what the transmission engine should do
  if (val < (_keelValue - 8))
  {
    // The value is smaller than the desired keel value, so rotate CW
    digitalWrite(SLACKING_KEEL_PIN, HIGH);
    digitalWrite(STRAINING_KEEL_PIN, LOW);
  }
  else if (val > (_keelValue + 8))  
  {    
    // The value is bigger than the desired keel value, so rotate CCW
    digitalWrite(SLACKING_KEEL_PIN, LOW);
    digitalWrite(STRAINING_KEEL_PIN, HIGH);
  }
  else
  {    
    // The value is within the margins of the desired value, so stop rotating
    digitalWrite(SLACKING_KEEL_PIN, LOW);
    digitalWrite(STRAINING_KEEL_PIN, LOW);    
  }
}

// Set the rudder position
void Boat::setRudderPosition(short newRudderPosition)
{
  if(_rudderPosition != newRudderPosition)
  {
    _rudderPosition = newRudderPosition;   // Save the given rudder position to the main variable
    rudderServo.attach(SERVO_RUDDER_PIN);  // Attach the rudder servo pin to the servo instance
    rudderServo.write(newRudderPosition);  // Write the new rudder position to the servo
  }
}

// Get and set for the sail position
uint8_t Boat::getSailPosition()
{
  // Convert the pot meter position of the sail to degrees
  short degreePosition = (_sailPosition) / (102.4 / 90);
  
  return (uint8_t)degreePosition;
}
void Boat::setSailPosition(short sailPosition)
{
  // If the sail state is manual, save the new sail position
  if(getSailingState() == 1)
  {
      // Convert the given sail position in degrees to a pot meter position
    float tempPosition = 0.80 * sailPosition;
    
    _sailPosition = (uint8_t)tempPosition;
  }
}

void Boat::checkSailPosition()
{
  // If the sail state is automatic, calculate the new sail position
  if(getSailingState() == 0)
  {
    float windhoek = getWindDirection();  // Get the wind direction
    
    if(_windDirectionOverflow == 1)
      windhoek += 256;
    
    if (windhoek > 180)
    {
      windhoek -= 360;                    // If the wind direction is bigger then 180, decrease it with 360
      windhoek *= -1;
    }
    
    // Save the new sail position to the main variable
    _sailPosition = 0.80 * (windhoek / 1.7578);
  }
  
  // Open the pin communication
  pinMode(SLACKING_SAIL_PIN, OUTPUT);
  pinMode(STRAINING_SAIL_PIN, OUTPUT);
  
  short val = analogRead(POT_MEASURER_SAIL_PIN);    // Read the input pin
  
  // Check what the transmission engine should do
  if (val < (_sailPosition - 3))
  {
    // The value is smaller than the desired sail position, so rotate CW
    digitalWrite(SLACKING_SAIL_PIN, HIGH);
    digitalWrite(STRAINING_SAIL_PIN, LOW);
  }
  else if (val > (_sailPosition + 3))  
  {
    // The value is bigger than the desired sail position, so rotate CCW
    digitalWrite(SLACKING_SAIL_PIN, LOW);
    digitalWrite(STRAINING_SAIL_PIN, HIGH);
  }
  else
  {
    // The value is within the margins of the desired value, so stop rotating
    digitalWrite(SLACKING_SAIL_PIN, LOW);
    digitalWrite(STRAINING_SAIL_PIN, LOW);    
  }
}

// Get and set for the battery voltage
uint8_t Boat::getBatteryVoltage()
{
  float batteryVoltage = analogRead(SENSOR_BATTERY_VOLTAGE_PIN);  // Read the ADC value (0-1024)
  
  Serial.print("Battery voltage: ");
  Serial.print(analogRead(SENSOR_BATTERY_VOLTAGE_PIN));
  Serial.println("");
  
  batteryVoltage *= 0.0196;                                       // Convert the ADC value to voltage  
  batteryVoltage *= 10;                                           // Multiply the battery voltage with ten, so we send a double with 2 decimals
  return (uint8_t)batteryVoltage;
}

// Get the inclination angle
uint8_t Boat::getInclinationAngle()
{
  _inclinationAngleOverflow = 0;                     // Initialize the inclination overflow to zero
  pinMode(SENSOR_WIND_PIN, INPUT);                   // Open the pin communication
  float duration = pulseIn(SENSOR_WIND_PIN, HIGH);   // Duration (value) of the incoming PWM signal
  short inclinationAngle = duration * 2.5352;        // Convert the duration value to degrees

  if(inclinationAngle > 256)                         // Check if the inclination is greater then 256
  {
    inclinationAngle -= 256;                         // If so, decrease the inclination with 256
    _inclinationAngleOverflow = 1;                   // Set the overflow byte to one
  }

  return (uint8_t)inclinationAngle;
}

// Get the wind direction
uint8_t Boat::getWindDirection()
{
  _windDirectionOverflow = 0;                       // Initialize the wind direction overflow to zero
  
  pinMode(SENSOR_WIND_PIN, INPUT);                  // Open the pin communication
  float total = 0;
  
  for(short i = 0; i < 10; i++)
    total += pulseIn(SENSOR_WIND_PIN, HIGH);   // Add the duration (value) of the incoming PWM signal to the total
  
  float average = total / 10;  
  short windDirection = average * 2.5352;           // Convert the duration value to degrees
  
  if(windDirection > 256)                           // Check if the wind direction is greater then 256
  {
    windDirection -= 256;                           // If so, decrease the wind direction with 256
    _windDirectionOverflow = 1;                     // Set the overflow byte to one
  }
  
  return (uint8_t)windDirection;
}

// Set the engine state
void Boat::setEngineState(uint8_t engineState)
{
  _engineState = engineState;  // Save the given engine state to the main variable
}

// Set the engine direction
void Boat::setEngineDirection(short engineDirection)
{
  _engineDirection = engineDirection;  // Save the given eingine direction to the main variable
  
  // Open the pin communication
  pinMode(PROPULSION_ENGINE_FORWARD,OUTPUT);
  pinMode(PROPULSION_ENGINE_BACKWARD,OUTPUT);
  
  // Check if the engine is turned on
  if(getEngineState() == 1)
  {    
    // Check if the engine direction
    if(engineDirection == 0)
    {
      digitalWrite(PROPULSION_ENGINE_BACKWARD, HIGH);
      digitalWrite(PROPULSION_ENGINE_FORWARD, LOW);
    }
    else
    {
      digitalWrite(PROPULSION_ENGINE_BACKWARD, LOW);
      digitalWrite(PROPULSION_ENGINE_FORWARD, HIGH);
    }
  }
  else
  {    
    // Engine should be off so turn off engine
    digitalWrite(PROPULSION_ENGINE_BACKWARD, LOW);
    digitalWrite(PROPULSION_ENGINE_FORWARD, LOW);
  }
}

// Get the heading
uint8_t Boat::getHeading()
{
  _headingOverflow = 0;                   // Initialize the heading overflow to zero
  byte highByte;                          // Ininitalize the highByte
  byte lowByte;                           // Ininitalize the lowByte

  Wire.beginTransmission(SENSOR_COMPASS_PIN);// Starts communication with the compass
  Wire.send(2);                           // Sends the register we wish to read
  Wire.endTransmission();                 // Ends the transmission

  Wire.requestFrom(SENSOR_COMPASS_PIN, 2);   // Requests the high byte

  while(Wire.available() < 2);            // Check if there is a byte to receive
  highByte = Wire.receive();              // Reads the highByte as an integer
  lowByte = Wire.receive();               // Reads the highByte as an integer

  short heading = ((highByte<<8)+lowByte) / 10;  // Converts bytes to a short

  if(heading > 256)                       // Check if the heading is greater then 256
  {
    heading -= 256;                       // If so, decrease the heading with 256
    _headingOverflow = 1;                 // Set the overflow to one
  }

  return (uint8_t)heading;
}

// Get the environment temperature
uint8_t Boat::getTemperatureEnvironment()
{
  _tempEnvironmentOverflow = 0;                                 // Initialize the environment temperature overflow to zero
  float temperatureEnvironment = 0;                             // To hold our environment temperature
  int sensorRead = analogRead(SENSOR_ENVIRONMENT_TEMP_PIN);     // Read the analog input pin

  for (int i=0; i <= 5; i++)
    temperatureEnvironment += (((sensorRead * 4.9) / 10) - 2);  // Add the current temperature to the total

  temperatureEnvironment /= 6;                                  // Devide the total temperature by the number of measurements for the average temperature
  temperatureEnvironment *= 10;                                 // Multiply the float by ten for float sending purposes
  short temperature = temperatureEnvironment;                   // Cast the temperature float to a short
  
  if(temperature > 256)                                         // Check if the temperature is greater then 256
  {
    temperature -= 256;                                         // If so, decrease the temperature with 256
    _tempEnvironmentOverflow = 1;                               // Set the overflow to one
  }
  
  return (uint8_t)temperature;
}

// Get the water temperature
uint8_t Boat::getTemperatureWater()
{
  _tempWaterOverflow = 0;                                 // Initialize the water temperature overflow to zero
  float temperatureWater = 0;                             // To hold our water temperature
  int sensorRead = analogRead(SENSOR_WATER_TEMP_PIN);     // Read the analog input pin

  for (int i=0; i <= 5; i++)
    temperatureWater += (((sensorRead * 4.9) / 10) - 2);  // Add the current temperature to the total

  temperatureWater /= 6;                                  // Devide the total temperature by the number of measurements for the average temperature
  temperatureWater *= 10;                                 // Multiply the float by ten for float sending purposes
  short temperature = temperatureWater;                   // Cast the temperature float to a short

  if(temperature > 256)                                   // Check if the temperature is greater then 256
  {
    temperature -= 256;                                   // If so, decrease the temperature with 256
    _tempWaterOverflow = 1;                               // Set the overflow to one
  }

  return (uint8_t)temperature;
}

// Read function for the RFID
void Boat::readRfid()
{  
  char code[10];             // Init the char code array with a length of 10
  int bytesread = 0;         // Init bytesread to zero
  Serial3.begin(2400);       // Open the serial communication to serial port 3
  int val = Serial3.read();  // Read the contents of serial port 3
  
  // Check if the read value is 10
  if(val == 10)
  {
    bytesread = 0;           // Reset the bytesread to zero
    while(bytesread < 10)    // Read 10 digit code
    {
      val = Serial3.read();  // Read the contents of serial port 3
      

      if((val == 10) || (val == 13)){break;}  // If header or stop bytes before the 10 digit reading, stop reading
      code[bytesread] = val; // Add the digit
      bytesread++;           // Increase the bytes read, ready to read the next digit
    }
    
    if(bytesread == 10)                     // Check if the number of bytes read is 10
    {
      if(strncmp(code, _rfidTag, 10) == 0)  // Compare the code and our RFID master tag
      {
        _rfidState = 1;                     // RFID tag matched, set the RFID state to 1
      }
    }
  }
  
  bytesread = 0;  // Reset bytes read to zero
}

// Activate the emergency stop
void  Boat::emergencyStop()
{
  pinMode(EMERGENCY_STOP_PIN, OUTPUT);     // Open the pin communication
  digitalWrite(EMERGENCY_STOP_PIN, HIGH);  // Trigger the emergency stop by sending a HIGH signal
}

