package androway.logging;

import android.app.Activity;
import android.content.ContentValues;
import android.text.format.DateFormat;
import android.widget.Toast;
import androway.common.Exceptions.MaxPoolSizeReachedException;
import androway.common.Exceptions.NotSupportedQueryTypeException;
import androway.database.DatabaseFactory;
import androway.database.DatabaseManager;
import androway.ui.R;
import java.util.Date;
import java.util.Map;

/**
 * Interface LoggingManager is.
 * @author Rinse
 * @since 02-03-2011
 * @version 0.41
 */
public class LoggingManager
{
	private static Activity _mainActivity;
	private static String _strFormat, _strAdded, _strRemoved;

	public static final String KEY_ROWID = "_id";
	public static final String KEY_TIME = "time";
	public static final String KEY_SUBJECT = "subject";
	public static final String KEY_MESSAGE = "message";
	public static final String TAG = "DBAdapter";
	public static final String DATABASE_NAME = "androway";
	public static final String DATABASE_TABLE = "logs";
	public static final String DATABASE_CREATE =
		"create table logs (_id integer primary key autoincrement, "
		+ "time text not null, subject text not null, "
		+ "message text not null);";

	private DatabaseManager myDbManager;

	public LoggingManager(Activity mainActivity) throws MaxPoolSizeReachedException
	{
		_mainActivity = mainActivity;

		DatabaseFactory.setMaxPoolSize(2);
		myDbManager = DatabaseFactory.acquireDatabaseManager(_mainActivity, "local");

		_strFormat = _mainActivity.getString(R.string.format);
		_strAdded = _mainActivity.getString(R.string.added);
		_strRemoved = _mainActivity.getString(R.string.removed);
	}

	public void addLog(String subject, String message) throws NotSupportedQueryTypeException
	{
		CharSequence formaat = _strFormat;
        Date date = new Date();
        CharSequence timeChar = DateFormat.format(formaat, date);
        String time = timeChar.toString();

		String query = "INSERT INTO " + DATABASE_TABLE +
					" VALUES (NULL, '" +
					time +
					"', '" +
					subject +
					"', '" +
					message +
					"')";

		myDbManager.executeNonQuery(query);
		Toast.makeText(_mainActivity, _strAdded, Toast.LENGTH_LONG).show();
	}

	public Map getLog(int logId)
	{
		String query = "SELECT * FROM " + DATABASE_TABLE + " WHERE ROWID = " + logId;

		Map<String, ContentValues> dataMap = myDbManager.getData(query);

		return dataMap;
	}

	public void clearAll() throws NotSupportedQueryTypeException
	{
		String query = "DELETE FROM " + DATABASE_TABLE;
		myDbManager.executeNonQuery(query);
		Toast.makeText(_mainActivity, _strRemoved, Toast.LENGTH_LONG).show();
	}
}