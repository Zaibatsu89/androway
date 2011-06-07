package proj.androway.logging;

import android.content.Context;
import proj.androway.common.Constants;
import proj.androway.common.Exceptions.MapIsEmptyException;
import proj.androway.common.Exceptions.MaxPoolSizeReachedException;
import proj.androway.common.Exceptions.NotSupportedQueryException;
import proj.androway.database.DatabaseFactory;
import proj.androway.database.IDatabaseManager;
import proj.androway.R;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import proj.androway.common.Exceptions.ConstructingLoggingManagerFailedException;
import proj.androway.common.SharedObjects;

/**
 * The LoggingManager class is a class for logging data to the desired database type
 * @author Rinse Cramer & Tymen Steur
 * @since 06-06-2011
 * @version 0.5
 */
public class LoggingManager
{
    private SharedObjects _sharedObjects;
    private Context _context;
    private IDatabaseManager _myDbManager;

    /**
     * The constructor for the LoggingManager. If the loggingType is DatabaseManagerBase.TYPE_HTTP,
     * the system will also login to the remote server.
     * @param sharedObjects An instance of the common SharedObjects object
     * @param context       The application context
     * @param loggingType   The logging type to use can be either DatabaseManagerBase.TYPE_HTTP or DatabaseManagerBase.TYPE_LOCAL
     */
    public LoggingManager(SharedObjects sharedObjects, Context context, String loggingType) throws ConstructingLoggingManagerFailedException
    {
        _sharedObjects = sharedObjects;
        _context = context;

        try
        {
            _myDbManager = DatabaseFactory.acquireDatabaseManager(_context, loggingType);

            // If initializing the database manager fails throw an exception.
            // This operation is mainly aimed at the login proces for the HttpManager.
            if (!_myDbManager.open())
                throw new ConstructingLoggingManagerFailedException(context.getString(R.string.ConstructingLoggingManagerFailedException));
        }
        catch (MaxPoolSizeReachedException ex)
        {
            Logger.getLogger(LoggingManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (ConstructingLoggingManagerFailedException ex)
        {
            Logger.getLogger(LoggingManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Add a new log to the database
     */
    public void addLog()
    {
        // Get the values for the log
        String sessionId = String.valueOf(_sharedObjects.session.sessionId);
        String leftWheel = String.valueOf(_sharedObjects.incomingData.leftWheelSpeed);
        String rightWheel = String.valueOf(_sharedObjects.incomingData.rightWheelSpeed);
        String inclination = String.valueOf(_sharedObjects.incomingData.inclination);

        // Assemble the insert query for the log
        String query = "INSERT INTO " + Constants.LOG_DB_TABLE + " (session_id, time, left_wheel, right_wheel, inclination) ";
        query += "VALUES (" + sessionId + ", '" + Math.round(new Date().getTime() / 1000) + "', " + leftWheel + ", " + rightWheel + ", " + inclination + ");";

        try
        {
            _myDbManager.executeNonQuery(Constants.LOG_DB_NAME, query);
        }
        catch (NotSupportedQueryException ex)
        {
            Logger.getLogger(LoggingManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Retreive the log with the given logId from the database
     * @param logId The id of the log
     * @return The database data of the log
     * @throws proj.androway.common.Exceptions.MapIsEmptyException Thrown when there is no data
     */
    public Map<String, Object> getLog(int logId) throws MapIsEmptyException
    {
        String query = "SELECT * FROM " + Constants.LOG_DB_TABLE + " WHERE log_id = " + String.valueOf(logId);
        Map<String, Object> data = _myDbManager.getData(Constants.LOG_DB_NAME, query);

        if (!data.isEmpty())
            return data;
        else
            throw new MapIsEmptyException(_context.getString(R.string.MapIsEmptyException));
    }

    /**
     * Retreive all logs from the database
     * @return The database data for all existing logs
     * @throws proj.androway.common.Exceptions.MapIsEmptyException Thrown when there is no data
     */
    public Map getLogs() throws MapIsEmptyException
    {
        String query = "SELECT * FROM " + Constants.LOG_DB_TABLE;
        Map<String, Object> data = _myDbManager.getData(Constants.LOG_DB_NAME, query);

        if (!data.isEmpty())
            return data;
        else
            throw new MapIsEmptyException(_context.getString(R.string.MapIsEmptyException));
    }

    /**
     * Delete all logs from te database
     */
    public void clearAll()
    {
        try
        {
            String query = "DELETE FROM " + Constants.LOG_DB_TABLE;
            _myDbManager.executeNonQuery(Constants.LOG_DB_NAME, query);
        }
        catch (NotSupportedQueryException ex)
        {
            Logger.getLogger(LoggingManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Get the total number of logs that are in the database
     * @return The number of logs
     */
    public int count()
    {
        String query = "SELECT COUNT(" + Constants.LOG_DB_ID_KEY + ") FROM " + Constants.LOG_DB_TABLE;
        Map<String, Object> data = _myDbManager.getData(Constants.LOG_DB_NAME, query);

        return Integer.valueOf(((Map<String, Object>)data.get("row0")).get("COUNT(" + Constants.LOG_DB_ID_KEY + ")").toString());
    }

    /**
     * Check if there are any logs in the database
     * @return Whether there are any logs in the database or not
     */
    public boolean isEmpty()
    {
        String query = "SELECT COUNT(" + Constants.LOG_DB_ID_KEY + ") FROM " + Constants.LOG_DB_TABLE;
        Map<String, Object> data = _myDbManager.getData(Constants.LOG_DB_NAME, query);

        return Integer.valueOf(((Map<String, Object>)data.get("row0")).get("COUNT(" + Constants.LOG_DB_ID_KEY + ")").toString()).equals(0);
    }

    /**
     * When a session is created, but the login process fails we will destroy this session again.
     * @param sessionId The session id to destroy
     * @param userId    The user id of the user that was running the session
     */
    public void destroyFailedSession(int sessionId, int userId)
    {
        if(sessionId != -1 && userId != -1)
        {            
            try
            {
                String query = "DELETE * FROM sessions WHERE session_id = " + sessionId + " AND user_id = " + userId;
                _myDbManager.executeNonQuery(Constants.LOG_DB_NAME, query);
            }
            catch (NotSupportedQueryException ex)
            {
                Logger.getLogger(LoggingManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}