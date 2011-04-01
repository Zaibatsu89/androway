package proj.androway.logging;

import android.content.Context;
import android.widget.Toast;
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

/**
 * Interface LoggingManager is.
 * @author Rinse
 * @since 17-03-2011
 * @version 0.43
 */
public class LoggingManager
{
    private static Context _context;
    private static String _strFormat, _strAdded, _strRemoved;

    private static final String _ID = "id";
    private static final String _TIME = "time";
    private static final String _SUBJECT = "subject";
    private static final String _MESSAGE = "message";

    private IDatabaseManager _myDbManager;

    public LoggingManager(Context context, String loggingType) throws MaxPoolSizeReachedException
    {
        _context = context;

        _myDbManager = DatabaseFactory.acquireDatabaseManager(_context, loggingType);

        _strFormat = _context.getString(R.string.format);
        _strAdded = _context.getString(R.string.added);
        _strRemoved = _context.getString(R.string.removed);
    }

    public void addLog(String subject, String message) throws NotSupportedQueryTypeException
    {
        DateFormat dateFormat = new SimpleDateFormat(_strFormat);
        Date date = new Date();
        String dateTime = dateFormat.format(date);

        StringBuilder builder = new StringBuilder();
        builder.append("INSERT INTO ");
        builder.append(Constants.DATABASE_TABLE);
        builder.append(" (");
        builder.append(_ID);
        builder.append(",");
        builder.append(_TIME);
        builder.append(",");
        builder.append(_SUBJECT);
        builder.append(",");
        builder.append(_MESSAGE);
        builder.append(") VALUES (NULL,'");
        builder.append(dateTime);
        builder.append("','");
        builder.append(subject);
        builder.append("','");
        builder.append(message);
        builder.append("')");
        String query = builder.toString();

        _myDbManager.executeNonQuery(Constants.DATABASE_NAME, query);
        Toast.makeText(_context, _strAdded, Toast.LENGTH_LONG).show();
    }

    public Map<String, Object> getLog(int logId) throws MapIsEmptyException
    {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT * FROM ");
        builder.append(Constants.DATABASE_TABLE);
        builder.append(" WHERE ");
        builder.append(_ID);
        builder.append("=");
        builder.append(logId);
        String query = builder.toString();

        Map<String, Object> data = _myDbManager.getData(Constants.DATABASE_NAME, query);

        if (!data.isEmpty())
                return data;
        else throw new MapIsEmptyException(_context.getString(R.string.MapIsEmptyException));
    }

    public Map getLogs() throws MapIsEmptyException
    {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT * FROM ");
        builder.append(Constants.DATABASE_TABLE);
        String query = builder.toString();

        Map<String, Object> data = _myDbManager.getData(Constants.DATABASE_NAME, query);

        if (!data.isEmpty())
                return data;
        else throw new MapIsEmptyException(_context.getString(R.string.MapIsEmptyException));
    }

    public void clearAll() throws NotSupportedQueryTypeException
    {
        StringBuilder builder = new StringBuilder();
        builder.append("DELETE FROM ");
        builder.append(Constants.DATABASE_TABLE);
        String query = builder.toString();
        _myDbManager.executeNonQuery(Constants.DATABASE_NAME, query);

        Toast.makeText(_context, _strRemoved, Toast.LENGTH_LONG).show();
    }

    public int count()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT COUNT(id) FROM ");
        builder.append(Constants.DATABASE_TABLE);
        String query = builder.toString();

        Map<String, Object> data = _myDbManager.getData(Constants.DATABASE_NAME, query);

        return Integer.valueOf(((Map<String, Object>)data.get("row0")).get("COUNT(id)").toString());
    }

    public boolean isEmpty()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT COUNT(id) FROM ");
        builder.append(Constants.DATABASE_TABLE);
        String query = builder.toString();

        Map<String, Object> data = _myDbManager.getData(Constants.DATABASE_NAME, query);

        return Integer.valueOf(((Map<String, Object>)data.get("row0")).get("COUNT(id)").toString()).equals(0);
    }
}