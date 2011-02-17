package androway.logging;

import android.app.Activity;
import android.database.Cursor;
import android.text.format.DateFormat;
import android.widget.Toast;
import androway.ui.R;
import java.util.Date;

/**
 * Class LocalManager stores log data on the Android device.
 * @author Rinse
 * @since 17-02-2011
 * @version 0.3
 */
public class LocalManager implements LoggingManager {
	private static Activity _mainActivity;
	private static DBAdapter _db;
	private static long _id;

	private static String _strFormat, _strAdded, _strXgot, _strLast, _strRemoved,
			_strXremoved, _strEmpty, _strId, _strTime, _strSubject, _strMessage;

	public LocalManager(Activity view) {
		_mainActivity = view;
		_db = new DBAdapter(_mainActivity);

		_strFormat = _mainActivity.getString(R.string.format);
		_strAdded = _mainActivity.getString(R.string.added);
		_strEmpty = _mainActivity.getString(R.string.empty);
		_strLast = _mainActivity.getString(R.string.last);
		_strRemoved = _mainActivity.getString(R.string.removed);
		_strXremoved = _mainActivity.getString(R.string.xremoved);
		
		_strId = _mainActivity.getString(R.string.id);
		_strTime = _mainActivity.getString(R.string.time);
		_strSubject = _mainActivity.getString(R.string.subject);
		_strMessage = _mainActivity.getString(R.string.message);
	}

	public void add(String subject, String message) {
		CharSequence formaat = _strFormat;
        Date date = new Date();
		CharSequence timeChar = DateFormat.format(formaat, date);
		String time = timeChar.toString();

		_db.open();
		_db.insertLog(time, subject, message);
		Toast.makeText(_mainActivity, _strAdded, Toast.LENGTH_LONG).show();
		_db.close();
		_id++;
	}

	public void get() {
		_db.open();
		Cursor c = _db.getLog(_id);
		if (c.moveToFirst())
			display(c);
		else
			Toast.makeText(_mainActivity, _strEmpty, Toast.LENGTH_LONG).show();
		_db.close();
	}

	public void remove() {
		if (_id > 0)
		{
			_db.open();
			if (_db.deleteLog(_id))
				Toast.makeText(_mainActivity, _strLast + "\n" + _id + "\n" + _strRemoved,
					Toast.LENGTH_LONG).show();
			else
				Toast.makeText(_mainActivity, _strLast + "\n" + _id + "\n" + _strXremoved,
					Toast.LENGTH_LONG).show();
			_db.close();
			_id--;
		}
		else
			Toast.makeText(_mainActivity, _strEmpty, Toast.LENGTH_LONG).show();
	}

	private void display(Cursor c) {
		Toast.makeText(_mainActivity,
			_strId + "\n" + c.getString(0) + "\n" +
			_strTime + "\n" + c.getString(1) + "\n" +
			_strSubject + "\n" + c.getString(2) + "\n" +
			_strMessage + "\n" + c.getString(3),
			Toast.LENGTH_LONG).show();
	}
}