package proj.androway.database;

import android.app.Application;
import android.content.Context;
import proj.androway.common.Exceptions.MaxPoolSizeReachedException;
import proj.androway.R;
import java.util.HashMap;
import java.util.Map;
import proj.androway.common.SharedObjects;

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
    private static Map _databaseManagersCollection = new HashMap();
    private static int _managerCount = 0;
    private static int _maxPoolSize = 2;

    private DatabaseFactory() {}

    public static DatabaseFactory getInstance()
    {
        if (_databaseFactory == null)
            _databaseFactory = new DatabaseFactory();

        return _databaseFactory;
    }

    public static IDatabaseManager acquireDatabaseManager(SharedObjects sharedObjects, Context context, String managerName) throws MaxPoolSizeReachedException
    {
        if (_managerCount <= _maxPoolSize)
        {
            IDatabaseManager databaseManager = null;

            if (_databaseManagersCollection.containsKey(managerName))
                databaseManager = (IDatabaseManager) _databaseManagersCollection.get(managerName);
            else
            {
                if (managerName.equals(DatabaseManagerBase.TYPE_HTTP))
                    databaseManager = new HttpManager(sharedObjects, context);
                else if (managerName.equals(DatabaseManagerBase.TYPE_LOCAL))
                    databaseManager = new LocalManager(sharedObjects, context);

                _databaseManagersCollection.put(managerName, databaseManager);
                _managerCount++;
            }

            return databaseManager;
        }
        else
            throw new MaxPoolSizeReachedException(context.getString(R.string.MaxPoolSizeReachedException));
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