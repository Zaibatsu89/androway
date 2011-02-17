package androway.connection;

import android.app.Activity;
import java.util.ArrayList;

/**
 * Class BluetoothManager sets up the bluetooth
 * connection between the Android device and the
 * mini Segway.
 * @author Rinse
 * @since 17-03-2011
 * @version 0.3
 */
public class BluetoothManager implements ConnectionManager
{
	private Activity _mainActivity;

	public BluetoothManager(Activity mainActivity) {
		_mainActivity = mainActivity;
	}


	public void open() {
		
	}

	public void close() {
		
	}

	public void post() {
		
	}

	public ArrayList get() {
		return new ArrayList();
	}
}