package proj.androway.connection;

/**
 * ConnectionManagerBase is an extension of the IConnectionManager interface.
 * Protected methods that are mandatory for the ConnectionManagers must be declared here.
 * @author Tymen
 * @since 17-03-2011
 * @version 0.1
 */
public abstract class ConnectionManagerBase implements IConnectionManager
{
	// All constants for the ConnectionManagers
	public static final String TYPE_BLUETOOTH = "bluetooth";
	public static final String TYPE_HTTP = "http";

	// Should be all protected accessible methods (so not accessible from the outside)
	// protected abstract void methodName();
}
