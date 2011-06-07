package proj.androway.database.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import proj.androway.common.Constants;
import java.util.HashMap;
import java.util.Map;
import proj.androway.common.Exceptions.NotSupportedQueryException;
import proj.androway.database.DatabaseManagerBase;

/**
 * The SqLiteManager class is the database manager for a local SQLite database
 * @author Rinse Cramer & Tymen Steur
 * @since 06-06-2011
 * @version 0.5
 */
public class SqLiteManager extends DatabaseManagerBase
{
    private SQLiteDatabase _db;
    private DatabaseHelper _dbHelper;

    /**
     * The constructor for the SqLiteManager
     * @param context   The application context
     */
    public SqLiteManager(Context context)
    {
        super(context);
        _dbHelper = new DatabaseHelper(context);
    }

    /**
     * Open (initialize) the local database manager
     * @return Whether the opening (initialization) was succesful or not
     */
    public boolean open()
    {
        // Get the actual database and store it
        _db = _dbHelper.getWritableDatabase();
        return true;
    }

    /**
     * Close the access to the local database
     */
    public void close()
    {
        _dbHelper.close();
    }

    /**
     * Execute the given SQL query (can be used for INSERT, UPDATE or DELETE commands)
     * @param dbName    The database name to perform the query on
     * @param query     The SQL query to execute
     * @return Whether the query was succesfully executed or not
     */
    public boolean executeNonQuery(String dbName, String query) throws NotSupportedQueryException
    {
        boolean result = false;

        try
        {
            _db.execSQL(query);
            result = true;
        }
        catch(SQLException e)
        {
            result = false;
        }

        return result;
    }

    /**
     * Get data based on the given SQL query
     * @param dbName    The database name to perform the query on
     * @param query     The SQL query to execute
     * @return The data result of the query
     */
    public Map<String, Object> getData(String dbName, String query)
    {
        Map<String, Object> retList = new HashMap<String, Object>();
        Map<String, Object> list;

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

        return retList;
    }

    /**
     * Class DatabaseHelper is a helper class to manage database creation and version management
     */
    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context)
        {
            super(context, Constants.LOG_DB_NAME, null, 1);
        }

        /**
         * When creating the DatabaseHelper, we also create a database for the application
         * @param db    The database
         */
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL("CREATE TABLE " + Constants.LOG_DB_TABLE + " (id INTEGER PRIMARY KEY, time TEXT NOT NULL, subject TEXT NOT NULL, message TEXT NOT NULL)");
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
    }
}