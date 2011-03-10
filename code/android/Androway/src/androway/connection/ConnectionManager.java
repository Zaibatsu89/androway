package androway.connection;

import java.util.ArrayList;
import org.apache.http.NameValuePair;

/**
 * Interface ConnectionManager is the interface for
 * the classes BluetoothManager and HttpManager.
 * @author Rinse
 * @since 10-03-2011
 * @version 0.42
 */
public interface ConnectionManager
{
	public abstract boolean open(String address);
	public abstract void close();
	public abstract boolean post(String address, ArrayList<NameValuePair> data);
	public abstract ArrayList get(String address, ArrayList<NameValuePair> params);
}