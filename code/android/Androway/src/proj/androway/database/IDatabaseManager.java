package proj.androway.database;

import proj.androway.common.Exceptions.NotSupportedQueryTypeException;
import java.util.Map;

/**
 * Interface IDatabaseManager is the interface for
 * the classes LocalManager and HttpManager.
 * @author Rinse
 * @since 28-02-2011
 * @version 0.3
 */
public interface IDatabaseManager
{
    public abstract boolean init();
    public abstract void close();
    public abstract boolean executeNonQuery(String dbName, String query) throws NotSupportedQueryTypeException;
    public abstract Map<String, Object> getData(String dbName, String query);
}