package org.me.android_experiment_03;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

/**
 *
 * @author Rinse
 */
public class Database extends Activity {
	private static DBAdapter db;
	private static long id;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        // ToDo add your GUI initialization code here
		setContentView(R.layout.main);
		db = new DBAdapter(this);
    }

	public void Add(View view) {
		db.open();
		id = db.insertTitle(
        		"0470565527",
        		"Professional Android 2 Application Development",
        		"John Wiley and Sons Ltd");
		Cursor c = db.getTitle(id);
		DisplayTitle(c);
        id = db.insertTitle(
        		"047077018X",
        		"Android Application Development For Dummies",
        		"For Dummies");
		c = db.getTitle(id);
		DisplayTitle(c);
		id = db.insertTitle(
				"1934356565",
				"Hello, Android",
				"Pragmatic Bookshelf");
		c = db.getTitle(id);
		DisplayTitle(c);
        db.close();
	}

	public void Get(View view) {
		db.open();
		Cursor c = db.getTitle(id);
		if (c.moveToFirst())
			DisplayTitle(c);
		else
			Toast.makeText(this, "No titles found",
					Toast.LENGTH_LONG).show();
		db.close();
	}

	public void Delete(View view) {
		db.open();
        if (db.deleteTitle(id))
            Toast.makeText(this, "Delete successful for id: " + id,
                Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this, "Delete failed for id: " + id,
                Toast.LENGTH_LONG).show();
        db.close();
		id--;
	}

	public void DisplayTitle(Cursor c) {
		Toast.makeText(this,
				"id: " + c.getString(0) + "\n" +
				"ISBN: " + c.getString(1) + "\n" +
                "TITLE: " + c.getString(2) + "\n" +
                "PUBLISHER:  " + c.getString(3),
                Toast.LENGTH_LONG).show();
	}
}