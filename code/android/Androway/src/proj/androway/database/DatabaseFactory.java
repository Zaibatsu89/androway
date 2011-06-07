package proj.androway.database;

import proj.androway.database.http.HttpManager;
import proj.androway.database.sqlite.SqLiteManager;
import android.app.Application;
import android.content.Context;
import proj.androway.common.Exceptions.MaxPoolSizeReachedException;
import proj.androway.R;
import java.util.HashMap;
import java.util.Map;

/**
 * The DatabaseFactory class is used for retreiving and managing the instances for
 * different databases and database types.
 * @author Rinse Cramer & Tymen Steur
 * @since 06-06-2011
 * @version 0.5
 */
public final class DatabaseFactory extends Application
{
    private static Map _databaseManagersCollection = new HashMap();
    private static int _managerCount = 0;
    private static int _maxPoolSize = 2;

    /**
     * Returns the desired IConnectionManager based on the given parameters
     * @param context       The application context
     * @param managerName   The name of the desired IDatabaseManager
     * @return The desired IDatabaseManager
     * @throws proj.androway.common.Exceptions.MaxPoolSizeReachedException Thrown when the 'object pool' exceeds the maximum pool size
     */
    public static IDatabaseManager acquireDatabaseManager(Context context, String managerName) throws MaxPoolSizeReachedException
    {
        IDatabaseManager databaseManager = null;

        // Check if the manager allready exists in the object pool
        if (_databaseManagersCollection.containsKey(managerName))
            databaseManager = (IDatabaseManager) _databaseManagersCollection.get(managerName);
        else
        {
            // The manager does not exist in the object pool yet, so create it
            if (managerName.equals(DatabaseManagerBase.TYPE_HTTP))
                databaseManager = new HttpManager(context);
            else if (managerName.equals(DatabaseManagerBase.TYPE_LOCAL))
                databaseManager = new SqLiteManager(context);

            // Check if by adding this manager, the object pool size will be exceeded or not
            if (_managerCount + 1 <= _maxPoolSize)
            {
                // Store the created manager in the object pool
                _databaseManagersCollection.put(managerName, databaseManager);
                _managerCount++;
            }
            else
                throw new MaxPoolSizeReachedException(context.getString(R.string.MaxPoolSizeReachedException));
        }

        return databaseManager;
    }

    /**
     * Remove the IDatabaseManager with the given manager name
     * @param managerName   The name (key) of the IDatabaseManager
     */
    public static void releaseDatabaseManager(String managerName)
    {
        if (_databaseManagersCollection.containsKey(managerName))
        {
            _databaseManagersCollection.remove(managerName);
            _managerCount--;
        }
    }

    /**
     * Set the maximum size of the object pool
     * @param maxPoolSize   The maximum size of the object pool
     */
    public static void setMaxPoolSize(int maxPoolSize)
    {
        _maxPoolSize = maxPoolSize;
    }
}