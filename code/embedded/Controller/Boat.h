/*
  Boat.cpp - Class for interacting with the physical boat.
*/

#ifndef Boat_h
#define Boat_h

#include <WProgram.h>
#include <Servo.h>

#define SENSOR_WATER_TEMP_PIN 6         // (Analog) pin for our water temperature sensor
#define SENSOR_ENVIRONMENT_TEMP_PIN 7   // (Analog) pin for our water temperature sensor
#define SENSOR_BATTERY_VOLTAGE_PIN 10   // (Analog) pin for our battery voltage
#define SENSOR_INCLINATION_PIN 34       // Pin for inclination sensor
#define SENSOR_WIND_PIN 35              // Pin for wind sensor
#define SENSOR_COMPASS_PIN 0x60         // Pin for for digital compass
#define SERVO_RUDDER_PIN 30             // Pin for rudder servo
#define EMERGENCY_STOP_PIN 22           // Pin for emergency stop
#define RFID_OUTPUT_PIN 36              // Pin for the RFID
#define SLACKING_KEEL_PIN 27            // Pin for slacking the keel
#define STRAINING_KEEL_PIN 26            // Pin for straining the keel
#define POT_MEASURER_KEEL_PIN 8         // Pin for measuring the keel position
#define SLACKING_SAIL_PIN 29            // Pin for slacking the sail
#define STRAINING_SAIL_PIN 28           // Pin for straining the sail
#define POT_MEASURER_SAIL_PIN 9         // Pin for measuring the sail position
#define PROPULSION_ENGINE_FORWARD 24    // Pin for the propulsion engine
#define PROPULSION_ENGINE_BACKWARD 25   // Pin for the propulsion engine


class Boat
{
  public:
    Boat();              // Empty constructor for the class
    
    // Temporary function to directly set the keel position ( for testing purposes )
    void tempKeelPosition(int position);    
    
    // Get and set for the sailing state
    uint8_t getSailingState(){return _sailingState;};
    void setSailingState(uint8_t sailingState);
    
    // Get and set for the keel position
    uint8_t getKeelPosition(){return (uint8_t)_keelPosition;};
    void setKeelPosition(short keelPosition);
    void checkKeelPosition();
    
    // Get and set for the rudder position
    uint8_t getRudderPosition(){return (uint8_t)_rudderPosition;};
    void setRudderPosition(short newRudderPosition);
    
    // Get and set for the sail position
    uint8_t getSailPosition();
    void setSailPosition(short sailPosition);
    void checkSailPosition();

    uint8_t getBatteryVoltage();                                              // Get the battery voltage
    uint8_t getInclinationAngle();                                            // Get the inclination angle
    uint8_t getInclinationAngleOverflow(){return _inclinationAngleOverflow;}; // Get the inclination overflow
    uint8_t getWindDirection();                                               // Get the wind direction
    uint8_t getWindDirectionOverflow(){return _windDirectionOverflow;};       // Get the wind direction overflow
    
    // Get and set for the engine state
    uint8_t getEngineState(){return _engineState;};
    void setEngineState(uint8_t engineState);
    
    // Get and set for the engine direction
    uint8_t getEngineDirection(){return (uint8_t)_engineDirection;};
    void setEngineDirection(short engineDirection);
    
    uint8_t getHeading();                                                   // Get the heading
    uint8_t getHeadingOverflow(){return _headingOverflow;};                 // Get the heading overflow
    uint8_t getTemperatureEnvironment();                                    // Get the environment temperature
    uint8_t getTemperatureEnvironmentOverflow(){return _tempEnvironmentOverflow;};  // Get the environment temperature overflow
    uint8_t getTemperatureWater();                                          // Get the water temperature
    uint8_t getTemperatureWaterOverflow(){return _tempWaterOverflow;};      // Get the water temperature overflow
    
    // Get, set and read for the RFID
    uint8_t getRfid(){return _rfidState;};
    void readRfid();
    
    void emergencyStop();               // Activate the emergency stop
    
  private:
    uint8_t _headingOverflow;           // Heading overflow byte
    uint8_t _tempEnvironmentOverflow;   // Environment temperature overflow byte
    uint8_t _tempWaterOverflow;         // Water temperature overflow byte
    uint8_t _windDirectionOverflow;     // Wind direction overflow byte
    uint8_t _inclinationAngleOverflow;  // Inclination overflow byte
    uint8_t _rfidState;                 // The rfid state (0 | 1)

    uint8_t _engineState;               // Main variable for the engine state
    short _engineDirection;             // Main variable for the engine direction
    short _keelPosition;                // Main variable for the keel position
    short _keelValue;                   // Main variable for the keel value
    short _rudderPosition;              // Main variable for the rudder position
    uint8_t _sailingState;              // Main variable for the sailing state
    short _sailPosition;                // Main variable for the sail position
    
    char* _rfidTag;                     // Main variable (pointer0 for the rfid tag
    Servo rudderServo;                  // Main variable for the rudder servo instance
};

#endif
