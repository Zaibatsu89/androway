package androway.common;

/**
 * Exceptions handles all exceptions inside Androway.
 * @author Tymen
 * @since 01-03-2011
 * @version 0.11
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
	 * Exception used for checking the contents of a map
	 */
	public static class ArrayListIsEmptyException extends Exception
	{
		public ArrayListIsEmptyException(String message)
		{
			super(message);
		}
	}
}