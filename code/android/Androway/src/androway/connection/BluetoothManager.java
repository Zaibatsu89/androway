package androway.connection;

import java.util.ArrayList;

/**
 * Class BluetoothManager sets up the bluetooth
 * connection between the Android device and the
 * mini Segway.
 * @author Rinse
 * @since 10-03-2011
 * @version 0.41
 */
public class BluetoothManager implements ConnectionManager
{
	public boolean open(String address) {
		return true;
	}

	public void close() {
		
	}

	public void post(String function, String dbName, String query) {

	}

	public ArrayList get(String function, String dbName, String query) {
		return new ArrayList();
	}
}