package androway.connection;

/**
 * Class ConnectionFactory uses:
 *		the factory method pattern,
 *		the singleton pattern and
 *		the objectpool pattern.
 * Childclass ConnectionManager is the interface for
 * the classes BluetoothManager and HttpManager.
 * @author Rinse
 * @since 16-02-2011
 * @version 0.2
 */
public final class ConnectionFactory {
	static ConnectionFactory connectionFactory;
	static ConnectionManager connectionManager;
	static int managerCount;

	private ConnectionFactory() {
		// init code
	}

	public ConnectionFactory getInstance() {
		return connectionFactory;
	}

	public static ConnectionManager acquireConnectionManager() {
		managerCount--;
		return connectionManager;
	}

	public static void releaseConnectionManager(ConnectionManager cm) {
		managerCount++;
		connectionManager = cm;
	}

	public static void setMaxPoolSize(int maxSize) {
		while (managerCount > maxSize)
			acquireConnectionManager();
	}
}