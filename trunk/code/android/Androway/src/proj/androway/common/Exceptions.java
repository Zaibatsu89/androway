package proj.androway.common;

/**
 * The Exceptions class contains all custom exceptions that are used in the application
 * @author Rinse Cramer & Tymen Steur
 * @since 06-06-2011
 * @version 0.5
 */
public class Exceptions
{
    /**
     * Exception used/thrown when a 'object pool' exceeds the maximum size
     */
    public static class MaxPoolSizeReachedException extends Exception
    {
        public MaxPoolSizeReachedException(String message) { super(message); }
    }

    /**
     * Exception used/thrown when a unsupported or bad query was given
     */
    public static class NotSupportedQueryException extends Exception
    {
        public NotSupportedQueryException(String message) { super(message); }
    }

    /**
     * Exception used/thrown when a Map object is empty
     */
    public static class MapIsEmptyException extends Exception
    {
        public MapIsEmptyException(String message) { super(message); }
    }

    /**
     * Exception used/thrown when a HttpPost request failed
     */
    public static class HttpPostRequestFailedException extends Exception
    {
        public HttpPostRequestFailedException(String message) { super(message); }
    }

    /**
     * Exception used/thrown when a HttpGet request failed
     */
    public static class HttpGetRequestFailedException extends Exception
    {
        public HttpGetRequestFailedException(String message) { super(message); }
    }

    /**
     * Exception used/thrown when the constructing of the LoggingManager object failed
     */
    public static class ConstructingLoggingManagerFailedException extends Exception
    {
        public ConstructingLoggingManagerFailedException(String message) { super(message); }
    }

    /**
     * Exception used/thrown when the connecting process of the bluetooth failed
     */
    public static class ConnectingBluetoothFailedException extends Exception
    {
        public ConnectingBluetoothFailedException(String message) { super(message); }
    }
}