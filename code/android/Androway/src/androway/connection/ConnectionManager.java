package androway.connection;

/**
 * Interface ConnectionManager is the interface for
 * the classes BluetoothManager and HttpManager.
 * @author Rinse
 * @since 16-02-2011
 * @version 0.2
 */
public interface ConnectionManager {
	public abstract void connect();
	public abstract void disconnect();
	public abstract void open();
	public abstract void close();
}