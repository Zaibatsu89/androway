package androway.database;

import android.app.Application;
import android.content.Context;
import androway.common.Exceptions.MaxPoolSizeReachedException;
import androway.ui.R;
import java.util.HashMap;
import java.util.Map;

/**
 * Class DatabaseFactory uses:
 *		the factory method pattern,
 *		the singleton pattern and
 *		the objectpool pattern.
 * Childclass DatabaseManager is the interface for
 * the classes LocalManager and HttpManager.
 * @author Rinse
 * @since 02-03-2011
 * @version 0.4
 */
public final class DatabaseFactory extends Application
{
	private static DatabaseFactory _databaseFactory;
	private static DatabaseManager _databaseManager;
	private static Map _databaseManagersCollection = new HashMap();
	private static int _managerCount;
	private static int _maxPoolSize;

	private DatabaseFactory() {}

	public static DatabaseFactory getInstance()
	{
		if (_databaseFactory == null)
			_databaseFactory = new DatabaseFactory();

		return _databaseFactory;
	}

	public static DatabaseManager acquireDatabaseManager(Context context, String managerName) throws MaxPoolSizeReachedException
	{
		if (_managerCount < _maxPoolSize)
		{
			if (_databaseManagersCollection.containsKey(managerName))
				_databaseManager = (DatabaseManager) _databaseManagersCollection.get(managerName);
			else
			{
				if (managerName.equals("http"))
					_databaseManager = new HttpManager(context);
				else if (managerName.equals("local"))
					_databaseManager = new LocalManager(context);

				_databaseManagersCollection.put(managerName, null);
				_managerCount++;
			}

			return _databaseManager;
		}
		else
		{
			throw new MaxPoolSizeReachedException(context.getString(R.string.MaxPoolSizeReachedException));
		}
	}

	public static void releaseDatabaseManager(String managerName)
	{
		if (_databaseManagersCollection.containsKey(managerName))
		{
			_databaseManagersCollection.remove(managerName);
			_managerCount--;
		}
	}

	public static void setMaxPoolSize(int maxPoolSize)
	{
		_maxPoolSize = maxPoolSize;
	}
}