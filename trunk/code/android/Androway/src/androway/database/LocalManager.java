package androway.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androway.common.Constants;
import java.util.HashMap;
import java.util.Map;

/**
 * Class LocalManager stores log data on the Android device.
 * @author Rinse
 * @since 09-03-2011
 * @version 0.42
 */
public class LocalManager implements IDatabaseManager {
	private SQLiteDatabase _db;
	private DatabaseHelper _dbHelper;
	private static String[] _dbColumns;

	public LocalManager(Context context, String[] dbColumns) {
		_dbHelper = new DatabaseHelper(context);
		_dbColumns = dbColumns;
	}

	private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context)
        {
            super(context, Constants.DATABASE_NAME, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
			//For developer phone
			db.execSQL("drop table if exists " + Constants.DATABASE_TABLE);

            db.execSQL("create table " +
				Constants.DATABASE_TABLE + " (" +
				_dbColumns[0] + " integer primary key, " +
				_dbColumns[1] + " text not null, " +
				_dbColumns[2] + " text not null, " +
				_dbColumns[3] + " text not null)");
        }

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
    }

	//---opens the database---
	private LocalManager open() throws SQLException {
		_db = _dbHelper.getWritableDatabase();
		return this;
	}

	//---closes the database---
	private void close() {
		_dbHelper.close();
	}

	public boolean executeNonQuery(String dbName, String query)
	{
		open();
		_db.execSQL(query);
		close();
		return true;
	}

	/**
	 * apply raw query and return result in list
	 * @param query
	 * @param selectionArgs
	 * @return Map<String, Object>
	 */
	public Map<String, Object> getData(String dbName, String query)
	{
	      Map<String, Object> retList = new HashMap<String, Object>();
	      Map<String, Object> list;

		  open();
	      Cursor cursor = _db.rawQuery(query, null);

	      if (cursor.moveToFirst())
		  {
			  int i = 0;
			  do
			  {
				  list = new HashMap<String, Object>();
				  
				  for(int j = 0; j < cursor.getColumnCount(); j++)
					  list.put(cursor.getColumnName(j),cursor.getString(j));
				  
				  retList.put("row" + i, list);

				  i++;
			  }
			  while (cursor.moveToNext());
	      }

	      if (cursor != null && !cursor.isClosed())
	         cursor.close();

		  close();

	      return retList;
	}
}