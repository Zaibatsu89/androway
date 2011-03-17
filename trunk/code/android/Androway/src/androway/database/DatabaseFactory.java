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
 * Childclass IDatabaseManager is the interface for
 * the classes LocalManager and HttpManager.
 * @author Rinse
 * @since 17-03-2011
 * @version 0.41
 */
public final class DatabaseFactory extends Application
{
	private static DatabaseFactory _databaseFactory;
	private static IDatabaseManager _databaseManager;
	private static Map _databaseManagersCollection = new HashMap();
	private static String[] _dbColumns = {"id", "time", "subject", "message"};
	private static int _managerCount;
	private static int _maxPoolSize = 2;

	private DatabaseFactory() {}

	public static DatabaseFactory getInstance()
	{
		if (_databaseFactory == null)
			_databaseFactory = new DatabaseFactory();

		return _databaseFactory;
	}

	public static IDatabaseManager acquireDatabaseManager(Context context, String managerName) throws MaxPoolSizeReachedException
	{
		if (_managerCount < _maxPoolSize)
		{
			if (_databaseManagersCollection.containsKey(managerName))
				_databaseManager = (IDatabaseManager) _databaseManagersCollection.get(managerName);
			else
			{
				if (managerName.equals(DatabaseManagerBase.TYPE_HTTP))
					_databaseManager = new HttpManager(context);
				else if (managerName.equals(DatabaseManagerBase.TYPE_LOCAL))
					_databaseManager = new LocalManager(context, _dbColumns);

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