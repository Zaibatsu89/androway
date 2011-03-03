package androway.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import androway.logging.LoggingManager;
import java.util.HashMap;
import java.util.Map;

/**
 * Class LocalManager stores log data on the Android device.
 * @author Rinse
 * @since 02-03-2011
 * @version 0.41
 */
public class LocalManager implements DatabaseManager {
	private DatabaseHelper _DBHelper;
	private SQLiteDatabase _db;
	private int _size;

	private Cursor _mCursor;
	private String[] _mColumnNames;
	private int _mKeyColumn;
	private Map<String, ContentValues> _mValues = null;

	public LocalManager(Context context) {
		_DBHelper = new DatabaseHelper(context);
	}

	private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context)
        {
            super(context, LoggingManager.DATABASE_NAME, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(LoggingManager.DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
        int newVersion)
        {
            Log.w(LoggingManager.TAG, "Upgrading database from version " + oldVersion
                    + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS titles");
            onCreate(db);
        }
    }

	//---opens the database---
	private LocalManager open() throws SQLException {
		_db = _DBHelper.getWritableDatabase();
		return this;
	}

	//---closes the database---
	private void close() {
		_DBHelper.close();
	}

	public void executeNonQuery(String query)
	{
		open();
		_db.execSQL(query);
		close();
	}

	public Map getData(String query) {
		open();
		_mCursor = _db.rawQuery(query, null);
		close();

		// Make a new map so old values returned by getRows() are undisturbed.
		int capacity = _mValues != null ? _mValues.size() : 0;
		_mValues = new HashMap<String, ContentValues>(capacity);
		while (_mCursor.moveToNext()) {
			ContentValues values = new ContentValues();
			for (int i = 0; i < _mColumnNames.length; i++) {
				if (i != _mKeyColumn) {
					values.put(_mColumnNames[i], _mCursor.getString(i));
				}
			}
			_mValues.put(_mCursor.getString(_mKeyColumn), values);
		}

		return _mValues;
	}
}