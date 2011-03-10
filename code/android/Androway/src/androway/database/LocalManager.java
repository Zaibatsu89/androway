package androway.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androway.logging.LoggingManager;
import java.util.ArrayList;

/**
 * Class LocalManager stores log data on the Android device.
 * @author Rinse
 * @since 09-03-2011
 * @version 0.42
 */
public class LocalManager implements DatabaseManager {
	private DatabaseHelper _DBHelper;
	private SQLiteDatabase _db;

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
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
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

	/**
	 * apply raw query and return result in list
	 * @param query
	 * @param selectionArgs
	 * @return ArrayList<ArrayList<String>>
	 */
	public ArrayList<ArrayList<String>> getData(String query) {
	      ArrayList<ArrayList<String>> retList = new ArrayList<ArrayList<String>>();
	      ArrayList<String> list = new ArrayList<String>();
		  open();
	      Cursor cursor = _db.rawQuery(query, null);
	      if (cursor.moveToFirst()) {
	         do {
	        	 list = new ArrayList<String>();
	        	 for(int i=0; i<cursor.getColumnCount(); i++){
	        		 list.add( cursor.getString(i) );
	        	 }
	        	 retList.add(list);
	         } while (cursor.moveToNext());
	      }
	      if (cursor != null && !cursor.isClosed()) {
	         cursor.close();
	      }
		  close();
	      return retList;
	}
}