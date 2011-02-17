package androway.common;

/**
 * Exceptions handles all exceptions inside Androway.
 * @author Tymen
 * @since 17-02-2011
 * @version 0.1
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
}