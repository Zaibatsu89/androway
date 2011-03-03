package androway.database;

import androway.common.Exceptions.NotSupportedQueryTypeException;
import java.util.Map;

/**
 * Interface DatabaseManager is the interface for
 * the classes LocalManager and HttpManager.
 * @author Rinse
 * @since 28-02-2011
 * @version 0.3
 */
public interface DatabaseManager {
	public abstract void executeNonQuery(String query) throws NotSupportedQueryTypeException;
	public abstract Map getData(String query);
}