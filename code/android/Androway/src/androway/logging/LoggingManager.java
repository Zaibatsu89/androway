package androway.logging;

import android.content.Context;
import android.text.format.DateFormat;
import android.widget.Toast;
import androway.common.Constants;
import androway.common.Exceptions.ArrayListIsEmptyException;
import androway.common.Exceptions.MaxPoolSizeReachedException;
import androway.common.Exceptions.NotSupportedQueryTypeException;
import androway.database.DatabaseFactory;
import androway.database.IDatabaseManager;
import androway.ui.R;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.TimeZone;

/**
 * Interface LoggingManager is.
 * @author Rinse
 * @since 09-03-2011
 * @version 0.42
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
		CharSequence formaat = _strFormat;
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+1:00"));
		cal.getTime().getTime();
        CharSequence timeChar = DateFormat.format(formaat, cal);
        String time = timeChar.toString();

		StringBuilder builder = new StringBuilder();
		builder.append("insert into ");
		builder.append(Constants.DATABASE_TABLE);
		builder.append(" (");
		builder.append(_ID);
		builder.append(",");
		builder.append(_TIME);
		builder.append(",");
		builder.append(_SUBJECT);
		builder.append(",");
		builder.append(_MESSAGE);
		builder.append(") values (null,'");
		builder.append(time);
		builder.append("','");
		builder.append(subject);
		builder.append("','");
		builder.append(message);
		builder.append("')");
		String query = builder.toString();

		_myDbManager.executeNonQuery(Constants.DATABASE_NAME, query);
		Toast.makeText(_context, _strAdded, Toast.LENGTH_LONG).show();
	}

	public Map<String, Object> getLog(int logId) throws ArrayListIsEmptyException
	{
		StringBuilder builder = new StringBuilder();
		builder.append("select * from ");
		builder.append(Constants.DATABASE_TABLE);
		builder.append(" where ");
		builder.append(_ID);
		builder.append("=");
		builder.append(logId);
		String query = builder.toString();

		Map<String, Object> data = _myDbManager.getData(Constants.DATABASE_NAME, query);

		if (!data.isEmpty())
			return data;
		else throw new ArrayListIsEmptyException(_context.getString(R.string.ArrayListIsEmptyException));
	}

	public void clearAll() throws NotSupportedQueryTypeException
	{
		StringBuilder builder = new StringBuilder();
		builder.append("delete from ");
		builder.append(Constants.DATABASE_TABLE);
		String query = builder.toString();
		_myDbManager.executeNonQuery(Constants.DATABASE_NAME, query);

		if (!isSQLiteSequenceEmpty()) {
			builder = new StringBuilder();
			builder.append("delete from sqlite_sequence where name='");
			builder.append(Constants.DATABASE_TABLE);
			builder.append("'");
			String query2 = builder.toString();
			_myDbManager.executeNonQuery(Constants.DATABASE_NAME, query2);
		}

		Toast.makeText(_context, _strRemoved, Toast.LENGTH_LONG).show();
	}

	public int count()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("select count(id) from ");
		builder.append(Constants.DATABASE_TABLE);
		String query = builder.toString();

		Map<String, Object> data = _myDbManager.getData(Constants.DATABASE_NAME, query);

		return Integer.valueOf(((Map<String, Object>)data.get("row0")).get("count(id)").toString());
	}

	public boolean isEmpty()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("select count(id) from ");
		builder.append(Constants.DATABASE_TABLE);
		String query = builder.toString();

		Map<String, Object> data = _myDbManager.getData(Constants.DATABASE_NAME, query);

		return Integer.valueOf(((Map<String, Object>)data.get("row0")).get("count(id)").toString()).equals(0);
	}

	public boolean isSQLiteSequenceEmpty()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("select count(name) from sqlite_master where name='sqlite_sequence'");
		String query = builder.toString();

		Map<String, Object> data = _myDbManager.getData(Constants.DATABASE_NAME, query);

		return Integer.valueOf(((Map<String, Object>)data.get("row0")).get("count(id)").toString()).equals(0);
	}
}