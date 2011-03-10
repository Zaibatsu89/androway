package androway.logging;

import android.app.Activity;
import android.text.format.DateFormat;
import android.widget.Toast;
import androway.common.Exceptions.ArrayListIsEmptyException;
import androway.common.Exceptions.MaxPoolSizeReachedException;
import androway.common.Exceptions.NotSupportedQueryTypeException;
import androway.database.DatabaseFactory;
import androway.database.DatabaseManager;
import androway.ui.R;
import java.util.ArrayList;
import java.util.Date;

/**
 * Interface LoggingManager is.
 * @author Rinse
 * @since 09-03-2011
 * @version 0.42
 */
public class LoggingManager
{
	private static Activity _mainActivity;
	private static String _strFormat, _strAdded, _strRemoved;

	public static final String KEY_ID = "id";
	public static final String KEY_TIME = "time";
	public static final String KEY_SUBJECT = "subject";
	public static final String KEY_MESSAGE = "message";
	public static final String DATABASE_NAME = "androway";
	public static final String DATABASE_TABLE = "logs";
	public static final String DATABASE_CREATE =
		"create table " +
		DATABASE_TABLE + " (" +
		KEY_ID + " integer primary key, " +
		KEY_TIME + " text not null, " +
		KEY_SUBJECT + " text not null, " +
		KEY_MESSAGE + " text not null);";
	public static final String DATABASE_DROP = "drop table if exists " + DATABASE_TABLE;

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

		StringBuilder builder = new StringBuilder();
		builder.append("insert into ");
		builder.append(DATABASE_TABLE);
		builder.append(" (");
		builder.append(KEY_ID);
		builder.append(",");
		builder.append(KEY_TIME);
		builder.append(",");
		builder.append(KEY_SUBJECT);
		builder.append(",");
		builder.append(KEY_MESSAGE);
		builder.append(") values (null,'");
		builder.append(time);
		builder.append("','");
		builder.append(subject);
		builder.append("','");
		builder.append(message);
		builder.append("')");
		String query = builder.toString();
		System.out.println("addLog query: " + query);

		myDbManager.executeNonQuery(query);
		Toast.makeText(_mainActivity, _strAdded, Toast.LENGTH_LONG).show();
	}

	public ArrayList<ArrayList<String>> getLog(int logId) throws ArrayListIsEmptyException
	{
		StringBuilder builder = new StringBuilder();
		builder.append("select * from ");
		builder.append(DATABASE_TABLE);
		builder.append(" where ");
		builder.append(KEY_ID);
		builder.append("=");
		builder.append(logId);
		String query = builder.toString();
		System.out.println("getLog query: " + query);

		ArrayList<ArrayList<String>> data = myDbManager.getData(query);

		if (!data.isEmpty())
			return data;
		else throw new ArrayListIsEmptyException("De ArrayList is leeg!");
	}

	public void clearAll() throws NotSupportedQueryTypeException
	{
		StringBuilder builder = new StringBuilder();
		builder.append("delete from ");
		builder.append(DATABASE_TABLE);
		String query = builder.toString();
		System.out.println("clearAll query1: " + query);

		builder = new StringBuilder();
		builder.append("delete from sqlite_sequence where name='");
		builder.append(DATABASE_TABLE);
		builder.append("'");
		String query2 = builder.toString();
		System.out.println("clearAll query2: " + query2);

		myDbManager.executeNonQuery(query);
		myDbManager.executeNonQuery(query2);
		Toast.makeText(_mainActivity, _strRemoved, Toast.LENGTH_LONG).show();
	}

	public boolean isEmpty()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("count * from ");
		builder.append(DATABASE_TABLE);
		String query = builder.toString();
		System.out.println("isEmpty query: " + query);

		ArrayList<ArrayList<String>> data = myDbManager.getData(query);

		return Integer.valueOf(data.get(0).get(0)).equals(0);
	}

	public int count()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("count * from ");
		builder.append(DATABASE_TABLE);
		String query = builder.toString();
		System.out.println("count query: " + query);

		ArrayList<ArrayList<String>> data = myDbManager.getData(query);

		return Integer.valueOf(data.get(0).get(0));
	}
}