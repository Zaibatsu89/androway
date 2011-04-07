package proj.androway.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import proj.androway.common.Constants;
import java.util.HashMap;
import java.util.Map;

/**
 * Class LocalManager stores log data on the Android device.
 * @author Rinse
 * @since 17-03-2011
 * @version 0.43
 */
public class LocalManager extends DatabaseManagerBase
{
	private SQLiteDatabase _db;
	private DatabaseHelper _dbHelper;

	public LocalManager(Context context)
	{
            _dbHelper = new DatabaseHelper(context);
	}

        // Simply return true, there is noting to initialize for the local manager
        public boolean init() { return true; }

	private static class DatabaseHelper extends SQLiteOpenHelper
        {
            DatabaseHelper(Context context)
            {
                super(context, Constants.DATABASE_NAME, null, 1);
            }

            @Override
            public void onCreate(SQLiteDatabase db)
            {
                            // Temporary: for developer phone
                            db.execSQL("DROP TABLE IF EXISTS " + Constants.DATABASE_TABLE);

                db.execSQL("CREATE TABLE " +
                                    Constants.DATABASE_TABLE + " (" +
                                    "id INTEGER PRIMARY KEY, " +
                                    "time TEXT NOT NULL, " +
                                    "subject TEXT NOT NULL, " +
                                    "message TEXT NOT NULL)");
            }

                    @Override
                    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
        }

	//---opens the database---
	public void open() throws SQLException
        {
            _db = _dbHelper.getWritableDatabase();;
	}

	//---closes the database---
	public void close()
        {
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