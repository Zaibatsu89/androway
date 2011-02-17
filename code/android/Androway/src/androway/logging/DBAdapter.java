package androway.logging;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * DBAdapter connects the LocalManager to the Android SQLite database.
 * @author Rinse
 * @since 17-02-2011
 * @version 0.3
 */
public class DBAdapter {
    public static final String KEY_ROWID = "_id";
    public static final String KEY_TIME = "time";
    public static final String KEY_SUBJECT = "subject";
    public static final String KEY_MESSAGE = "message";
    private static final String TAG = "DBAdapter";
    private static final String DATABASE_NAME = "androway";
    private static final String DATABASE_TABLE = "logs";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE =
		"create table logs (_id integer primary key autoincrement, "
		+ "time text not null, subject text not null, "
		+ "message text not null);";

    private final Context context;

    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public DBAdapter(Context ctx)
    {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
        int newVersion)
        {
            Log.w(TAG, "Upgrading database from version " + oldVersion
                    + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS titles");
            onCreate(db);
        }
    }

    //---opens the database---
    public DBAdapter open() throws SQLException
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //---closes the database---
    public void close()
    {
        DBHelper.close();
    }

    //---insert a title into the database---
    public long insertLog(String time, String subject, String message)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TIME, time);
        initialValues.put(KEY_SUBJECT, subject);
        initialValues.put(KEY_MESSAGE, message);
        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    //---deletes a particular title---
    public boolean deleteLog(long rowId)
    {
        return db.delete(DATABASE_TABLE, KEY_ROWID +
        		"=" + rowId, null) > 0;
    }

    //---retrieves all the titles---
    public Cursor getAllLogs()
    {
        return db.query(DATABASE_TABLE, new String[] {
        		KEY_ROWID,
        		KEY_TIME,
        		KEY_SUBJECT,
                KEY_MESSAGE},
                null,
                null,
                null,
                null,
                null);
    }

    //---retrieves a particular title---
    public Cursor getLog(long rowId) throws SQLException
    {
        Cursor mCursor =
                db.query(true, DATABASE_TABLE, new String[] {
                		KEY_ROWID,
                		KEY_TIME,
                		KEY_SUBJECT,
                		KEY_MESSAGE
                		},
                		KEY_ROWID + "=" + rowId,
                		null,
                		null,
                		null,
                		null,
                		null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //---updates a title---
    public boolean updateLog(long rowId, String time,
    String subject, String message)
    {
        ContentValues args = new ContentValues();
        args.put(KEY_TIME, time);
        args.put(KEY_SUBJECT, subject);
        args.put(KEY_MESSAGE, message);
        return db.update(DATABASE_TABLE, args,
                         KEY_ROWID + "=" + rowId, null) > 0;
    }
}