package proj.androway.connection;

import java.util.ArrayList;
import java.util.Map;
import org.apache.http.NameValuePair;

/**
 * Class BluetoothManager sets up the bluetooth
 * connection between the Android device and the
 * mini Segway.
 * @author Rinse
 * @since 10-03-2011
 * @version 0.41
 */
public class BluetoothManager extends ConnectionManagerBase
{
	public void run() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public boolean open(String address, ArrayList<NameValuePair> data) {
		synchronized(this) {
			throw new UnsupportedOperationException("Not supported yet.");
		}
	}

	public void close() {
		synchronized(this) {
			throw new UnsupportedOperationException("Not supported yet.");
		}
	}

	public boolean post(String address, ArrayList<NameValuePair> data) {
		synchronized(this) {
			throw new UnsupportedOperationException("Not supported yet.");
		}
	}

	public Map<String, Object> get(String address, ArrayList<NameValuePair> params) {
		synchronized(this) {
			throw new UnsupportedOperationException("Not supported yet.");
		}
	}
}