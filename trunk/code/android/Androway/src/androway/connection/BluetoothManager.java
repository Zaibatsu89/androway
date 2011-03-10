package androway.connection;

import java.util.ArrayList;
import org.apache.http.NameValuePair;

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
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void close() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public boolean post(String address, ArrayList<NameValuePair> data) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public ArrayList get(String address, ArrayList<NameValuePair> params) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}