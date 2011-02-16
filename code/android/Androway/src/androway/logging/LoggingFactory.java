package androway.logging;

/**
 * Class LoggingFactory uses:
 *		the factory method pattern,
 *		the singleton pattern and
 *		the objectpool pattern.
 * Childclass LoggingManager is the interface for
 * the classes LocalManager and HttpManager.
 * @author Rinse
 * @since 16-02-2011
 * @version 0.2
 */
public final class LoggingFactory {
	static LoggingFactory loggingFactory;
	static LoggingManager loggingManager;
	static int managerCount;

	private LoggingFactory() {
		// init code
	}

	public LoggingFactory getInstance() {
		return loggingFactory;
	}

	public static LoggingManager acquireLoggingManager() {
		managerCount--;
		return loggingManager;
	}

	public static void releaseLoggingManager(LoggingManager cm) {
		managerCount++;
		loggingManager = cm;
	}

	public static void setMaxPoolSize(int maxSize) {
		while (managerCount > maxSize)
			acquireLoggingManager();
	}
}