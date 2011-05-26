package proj.androway.logging;

import android.content.Context;
import proj.androway.common.Constants;
import proj.androway.common.Exceptions.MapIsEmptyException;
import proj.androway.common.Exceptions.MaxPoolSizeReachedException;
import proj.androway.common.Exceptions.NotSupportedQueryTypeException;
import proj.androway.database.DatabaseFactory;
import proj.androway.database.IDatabaseManager;
import proj.androway.R;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import proj.androway.common.Exceptions.ConstructingLoggingManagerFailedException;
import proj.androway.common.SharedObjects;

/**
 * Interface LoggingManager is.
 * @author Rinse
 * @since 17-03-2011
 * @version 0.43
 */
public class LoggingManager
{
    private SharedObjects _sharedObjects;
    private Context _context;
    private String _strDateFormat;
    private IDatabaseManager _myDbManager;

    public LoggingManager(SharedObjects sharedObjects, Context context, String loggingType) throws MaxPoolSizeReachedException, ConstructingLoggingManagerFailedException
    {
        _sharedObjects = sharedObjects;
        _context = context;

        _myDbManager = DatabaseFactory.acquireDatabaseManager(_sharedObjects, _context, loggingType);

        // If initializing the database manager fails throw an exception.
        // This operation is mainly aimed at the login proces for the HttpManager.
        if(!_myDbManager.init())
            throw new ConstructingLoggingManagerFailedException(context.getString(R.string.ConstructingLoggingManagerFailedException));

        _strDateFormat = _context.getString(R.string.format);
    }

    public void addLog() throws NotSupportedQueryTypeException
    {
        DateFormat dateFormat = new SimpleDateFormat(_strDateFormat);
        Date date = new Date();
        String dateTime = dateFormat.format(date);
        
        // Get the values for the log
        String sessionId = String.valueOf(_sharedObjects.session.sessionId);
        String leftWheel = String.valueOf(_sharedObjects.incomingData.leftWheelSpeed);
        String rightWheel = String.valueOf(_sharedObjects.incomingData.rightWheelSpeed);
        String inclination = String.valueOf(_sharedObjects.incomingData.inclination);

        // Assemble the insert query for the log
        String query = "INSERT INTO " + Constants.LOG_DB_TABLE + " (session_id, time, left_wheel, right_wheel, inclination) ";
        query += "VALUES (" + sessionId + ", '" + dateTime + "', " + leftWheel + ", " + rightWheel + ", " + inclination + ");";

        _myDbManager.executeNonQuery(Constants.LOG_DB_NAME, query);
    }

    public Map<String, Object> getLog(int logId) throws MapIsEmptyException
    {
        String query = "SELECT * FROM " + Constants.LOG_DB_TABLE + " WHERE log_id = " + String.valueOf(logId);
        Map<String, Object> data = _myDbManager.getData(Constants.LOG_DB_NAME, query);

        if (!data.isEmpty())
                return data;
        else throw new MapIsEmptyException(_context.getString(R.string.MapIsEmptyException));
    }

    public Map getLogs() throws MapIsEmptyException
    {
        String query = "SELECT * FROM " + Constants.LOG_DB_TABLE;
        Map<String, Object> data = _myDbManager.getData(Constants.LOG_DB_NAME, query);

        if (!data.isEmpty())
            return data;
        else throw new MapIsEmptyException(_context.getString(R.string.MapIsEmptyException));
    }

    public void clearAll() throws NotSupportedQueryTypeException
    {
        String query = "DELETE FROM " + Constants.LOG_DB_TABLE;
        _myDbManager.executeNonQuery(Constants.LOG_DB_NAME, query);
    }

    public int count()
    {
        String query = "SELECT COUNT(" + Constants.LOG_DB_ID_KEY + ") FROM " + Constants.LOG_DB_TABLE;
        Map<String, Object> data = _myDbManager.getData(Constants.LOG_DB_NAME, query);

        return Integer.valueOf(((Map<String, Object>)data.get("row0")).get("COUNT(" + Constants.LOG_DB_ID_KEY + ")").toString());
    }

    public boolean isEmpty()
    {
        String query = "SELECT COUNT(" + Constants.LOG_DB_ID_KEY + ") FROM " + Constants.LOG_DB_TABLE;
        Map<String, Object> data = _myDbManager.getData(Constants.LOG_DB_NAME, query);

        return Integer.valueOf(((Map<String, Object>)data.get("row0")).get("COUNT(" + Constants.LOG_DB_ID_KEY + ")").toString()).equals(0);
    }

    public void destroyFailedSession(int sessionId, int userId) throws NotSupportedQueryTypeException
    {
        String query = "DELETE * FROM sessions WHERE session_id = " + sessionId + " AND user_id = " + userId;
        _myDbManager.executeNonQuery(Constants.LOG_DB_NAME, query);
    }
}