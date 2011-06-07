package proj.androway.connection.bluetooth;

/**
 * The OutgoingData class is the class used for holding the outgoing bluetooth data of the application
 * @author Rinse Cramer & Tymen Steur
 * @since 06-06-2011
 * @version 0.5
 */
public class OutgoingData
{
    /**
     * The driving speed for the Androway. Value from -100 to +100.
     */
    public float drivingSpeed;

    /**
     * The driving direction for the Androway. Value from -100 to +100.
     */
    public float drivingDirection;

    /**
     * Whether to put the Androway on hold or not. Value should be 0 || 1.
     */
    public short onHold;

    /**
     * Whether to stop the session of the Androway or not. Value should be 0 || 1.
     */
    public short stopSession;

    /**
     * Whether to perform a 360 or not
     */
    public short do360;

    /**
     * Initialize the outgoing data values with init values
     */
    public OutgoingData()
    {
        drivingSpeed = 0.0f;
        drivingDirection = 0.0f;
        onHold = 1;
        stopSession = 0;
        do360 = 0;
    }
}