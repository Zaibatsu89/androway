package proj.androway.database;

import proj.androway.common.Exceptions.NotSupportedQueryException;
import java.util.Map;

/**
 * IDatabaseManager is the interface used for all types of databases (f.e. local SQLite, http MySql, ...)
 * @author Rinse Cramer & Tymen Steur
 * @since 06-06-2011
 * @version 0.5
 */
public interface IDatabaseManager
{
    /**
     * Open (initialize) the database connection
     * @return Whether the opening (initialization) was succesful or not
     */
    public abstract boolean open();

    /**
     * Close the database connection
     */
    public abstract void close();

    /**
     * Execute the given SQL query (can be used for INSERT, UPDATE or DELETE commands)
     * @param dbName    The database name to perform the query on
     * @param query     The SQL query to execute
     * @return Whether the query was succesfully executed or not
     * @throws proj.androway.common.Exceptions.NotSupportedQueryException Thrown when the given query has a bad format or is not supported
     */
    public abstract boolean executeNonQuery(String dbName, String query) throws NotSupportedQueryException;

    /**
     * Get data based on the given SQL query
     * @param dbName    The database name to perform the query on
     * @param query     The SQL query to execute
     * @return The data result of the query
     */
    public abstract Map<String, Object> getData(String dbName, String query);
}