package androway.connection;

import java.util.ArrayList;

/**
 * Interface ConnectionManager is the interface for
 * the classes BluetoothManager and HttpManager.
 * @author Rinse
 * @since 18-02-2011
 * @version 0.4
 */
public interface ConnectionManager
{
	public abstract boolean open(String address);
	public abstract void close();
	public abstract void post(ArrayList data);
	public abstract ArrayList get();
}