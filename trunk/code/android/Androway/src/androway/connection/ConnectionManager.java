package androway.connection;

import java.util.ArrayList;

/**
 * Interface ConnectionManager is the interface for
 * the classes BluetoothManager and HttpManager.
 * @author Rinse
 * @since 10-03-2011
 * @version 0.41
 */
public interface ConnectionManager
{
	public abstract boolean open(String address);
	public abstract void close();
	public abstract void post(String function, String dbName, String query);
	public abstract ArrayList get(String function, String dbName, String query);
}