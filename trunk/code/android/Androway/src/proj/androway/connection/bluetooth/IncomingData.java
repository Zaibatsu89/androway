package proj.androway.connection.bluetooth;

/**
 * The IncomingData class is the class used for holding the incoming bluetooth data of the application
 * @author Rinse Cramer & Tymen Steur
 * @since 06-06-2011
 * @version 0.5
 */
public class IncomingData
{
    /**
     * The speed of the left wheel of the Androway
     */
    public int leftWheelSpeed;

    /**
     * The speed of the right wheel of the Androway
     */
    public int rightWheelSpeed;

    /**
     * The inclination angle of the Androway
     */
    public float inclination;

    /**
     * The battery voltage of the Androway in percentages
     */
    public int batteryVoltage;

    /**
     * Initialize the incoming data values with init values
     */
    public IncomingData()
    {
        leftWheelSpeed = 0;
        rightWheelSpeed = 0;
        inclination = 0.0f;
        batteryVoltage = -1;
    }
}