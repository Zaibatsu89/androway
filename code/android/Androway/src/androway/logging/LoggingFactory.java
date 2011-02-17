package androway.logging;

import androway.common.Exceptions.MaxPoolSizeReachedException;
import androway.ui.R;
import java.util.HashMap;
import java.util.Map;

/**
 * Class LoggingFactory uses:
 *		the factory method pattern,
 *		the singleton pattern and
 *		the objectpool pattern.
 * Childclass LoggingManager is the interface for
 * the classes LocalManager and HttpManager.
 * @author Rinse
 * @since 17-02-2011
 * @version 0.3
 */
public final class LoggingFactory
{
	private static LoggingFactory _loggingFactory;
	private static Map _loggingManagersCollection;
	private static int _managerCount;
	private static int _maxPoolSize;

	private LoggingFactory()
	{
		_loggingManagersCollection = new HashMap();
	}

	public LoggingFactory getInstance()
	{
		if (_loggingFactory == null)
			_loggingFactory = new LoggingFactory();

		return _loggingFactory;
	}

	public static LoggingManager acquireLoggingManager(String managerName) throws MaxPoolSizeReachedException
	{
		if (_managerCount < _maxPoolSize)
		{
			LoggingManager lm = null;

			if (_loggingManagersCollection.containsKey(managerName))
				lm = (LoggingManager) _loggingManagersCollection.get(managerName);
			else
			{
				if (managerName.equals("http"))
					lm = new HttpManager();
				else if (managerName.equals("local"))
					lm = new LocalManager();

				_managerCount++;
			}

			return lm;
		}
		else
			throw new MaxPoolSizeReachedException(String.valueOf(R.string.MaxPoolSizeReachedException));
	}

	public static void releaseLoggingManager(String managerName)
	{
		if (_loggingManagersCollection.containsKey(managerName))
		{
			_loggingManagersCollection.remove(managerName);
			_managerCount--;
		}
	}

	public static void setMaxPoolSize(int maxPoolSize)
	{
		_maxPoolSize = maxPoolSize;
	}
}