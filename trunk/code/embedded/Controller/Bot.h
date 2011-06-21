#ifndef Bot_h
#define Bot_h

#include <AFMotor.h>
#include <WProgram.h>

/**
 * The number of values to use for the average calculation for the battery voltage
 */
#define VOLTAGE_AVERAGE_SPREAD 30

/**
 * The IncomingData object used for storing the received data
 * @author Tymen Steur
 * @since 06-06-2011
 * @version 0.3
 */
typedef struct IncomingData
{
  /**
   * The direction for the bot. Value -100 to 100.
   */
  float drivingDirection;
  
  /**
   * The speed for the bot. Value from -100 to 100.
   */
  float drivingSpeed;
  
  /**
   * Whether to perform a 360 or not
   */
  boolean do360;
  
  /**
   * Wheter to pause or not
   */
  boolean isOnHold;
  
  /**
   * Whether to stop the session or not
   */
  boolean stopSession;
};

/**
 * The OutgoingData object used for storing the data that needs to be sent
 * @author Tymen Steur
 * @since 06-06-2011
 * @version 0.3
 */
typedef struct OutgoingData
{
  /**
   * The amount of throttle on the left wheel (motor). Percentage from 0 - 100.
   */
  int leftWheel;
  
  /**
   * The amount of throttle on the right wheel (motor). Percentage from 0 - 100.
   */
  int rightWheel;
  
  /**
   * The inclination of the bot in degrees.
   */
  float inclination;
  
  /**
   * The remaining battery voltage. Percentage from 0 - 100.
   */
  int batteryVoltage;
};

/**
 * The Bot class for interacting with the physical balancing bot.
 * @author Tymen Steur
 * @since 06-06-2011
 * @version 0.3
 */
class Bot
{
  private:
    // Private variables
    /**
     * The IncomingData object used for storing the received data
     */
    IncomingData _incomingData;
    
    /**
     * The OutgoingData object used for storing the data that needs to be sent
     */
    OutgoingData _outgoingData;
    
    /**
     * The DC motor object for the left motor of the bot
     */
    AF_DCMotor _leftMotor;
    
    /**
     * The DC motor object for the right motor of the bot
     */
    AF_DCMotor _rightMotor;
    
    /**
     * The number of the left motor on the shield
     */
    byte _leftMotorNr;
    
    /**
     * The number of the right motor on the shield
     */
    byte _rightMotorNr;
    
    /**
     * The analog pin number from where the the battery voltage can be measured
     */
    byte _batteryVoltagePort;
    
    /**
     * A flag for whether the motors are running or not
     */
    boolean _motorRunning;
    
    // Private functions
    /**
     * Handles the motion/driving of the bot. Triggers the motors.
     */
    void handleMotion();        // Handles the motion/driving of the bot. Triggers the engines.
    
    /**
     * Get the remaining battery voltage of the bot
     * @return The percentage of power left in the battery
     */
    int getBatteryVoltage();
    
  public:
    /**
     * The constructor for the Bot
     * @param leftMotorNr        The number of the left motor on the motor shield
     * @param rightMotorNr       The number of the right motor on the motor shield
     * @param batteryVoltagePort The analog pin number from where the the battery voltage can be measured
     */
    Bot(byte leftMotorNr, byte rightMotorNr, byte batteryVoltagePort) : _leftMotor(leftMotorNr, MOTOR12_1KHZ), _rightMotor(rightMotorNr, MOTOR12_1KHZ)
    {
      // Initialize some variables that are used by the class
      IncomingData _incomingData = {};
      _incomingData.isOnHold = true;  // The bot is set on hold by default
      _incomingData.do360 = false;    // The bot doesn't do a 360 by default
      
      OutgoingData _outgoingData = {};
      
      _leftMotorNr = leftMotorNr;
      _rightMotorNr = rightMotorNr;
      _batteryVoltagePort = batteryVoltagePort;
      
      _motorRunning = false;
    };

    /**
     * Get the data that needs to be sent towards the remote device
     * @return The OutgoingData object that holds the data that needs to be sent
     */
    struct OutgoingData getOutgoingData();
    
    /**
     * Set (store) the IncomingData object that holds the received data from the remote device
     * @param botData  The IncomingData object that holds the received data
     */
    void setIncomingData(struct IncomingData botData);
    
    /**
     * Stop the currently running session
     */
    void stopRunningSession();
    
    /**
     * A function that does the same as the Arduino - map() function, but now it takes floats.
     * <pre>
     * http://www.arduino.cc/en/Reference/Map
     * "Re-maps a number from one range to another.
     *  That is, a value of fromLow would get mapped to toLow,
     *  a value of fromHigh to toHigh, values in-between to values in-between, etc."
     * </pre>
     */
    float mapFloat(float x, float in_min, float in_max, float out_min, float out_max);  // A version of the Arduino map() function that accepts floats
};

#endif
