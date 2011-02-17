package androway.logging;

/**
 * Interface LoggingManager is the interface for
 * the classes LocalManager and HttpManager.
 * @author Rinse
 * @since 17-02-2011
 * @version 0.2
 */
public interface LoggingManager {
	public abstract void add(String subject, String message);
	public abstract void get();
	public abstract void remove();
}