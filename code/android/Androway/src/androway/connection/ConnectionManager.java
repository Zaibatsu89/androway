package androway.connection;

import java.util.ArrayList;

/**
 * Interface ConnectionManager is the interface for
 * the classes BluetoothManager and HttpManager.
 * @author Rinse
 * @since 17-02-2011
 * @version 0.3
 */
public interface ConnectionManager
{
	public abstract void open();
	public abstract void close();
	public abstract void post();
	public abstract ArrayList get();
}