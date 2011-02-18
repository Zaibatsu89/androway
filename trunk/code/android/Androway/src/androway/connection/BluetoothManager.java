package androway.connection;

import android.app.Activity;
import java.util.ArrayList;

/**
 * Class BluetoothManager sets up the bluetooth
 * connection between the Android device and the
 * mini Segway.
 * @author Rinse
 * @since 18-03-2011
 * @version 0.4
 */
public class BluetoothManager implements ConnectionManager
{
	private Activity _mainActivity;

	public BluetoothManager(Activity mainActivity) {
		_mainActivity = mainActivity;
	}

	public boolean open(String address) {
		return true;
	}

	public void close() {
		
	}

	public void post(ArrayList data) {
		
	}

	public ArrayList get() {
		return new ArrayList();
	}
}