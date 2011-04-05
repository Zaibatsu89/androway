package proj.androway.common;

/**
 * Exceptions handles all exceptions inside Androway.
 * @author Tymen en Rinse
 * @since 17-03-2011
 * @version 0.13
 */
public class Exceptions
{
	/**
	 * Exception used for checking the maximum object pool size
	 */
	public static class MaxPoolSizeReachedException extends Exception
	{
            public MaxPoolSizeReachedException(String message)
            {
                super(message);
            }
	}

	/**
	 * Exception used for checking the supported query type
	 */
	public static class NotSupportedQueryTypeException extends Exception
	{
            public NotSupportedQueryTypeException(String message)
            {
                super(message);
            }
	}

	/**
	 * Exception used for checking the contents of an ArrayList
	 */
	public static class MapIsEmptyException extends Exception
	{
            public MapIsEmptyException(String message)
            {
                super(message);
            }
	}

	/**
	 * Exception used for checking if a HttpPost request failed
	 */
	public static class HttpPostRequestFailedException extends Exception
	{
            public HttpPostRequestFailedException(String message)
            {
                super(message);
            }
	}

	/**
	 * Exception used for checking if an HttpGet request failed
	 */
	public static class HttpGetRequestFailedException extends Exception
	{
            public HttpGetRequestFailedException(String message)
            {
                super(message);
            }
	}

        /**
	 * Exception used when the the constructing of the logging manager fails
	 */
	public static class ConstructingLoggingManagerFailedException extends Exception
	{
            public ConstructingLoggingManagerFailedException(String message)
            {
                super(message);
            }
	}
}