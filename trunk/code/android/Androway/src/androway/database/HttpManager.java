package androway.database;

import android.content.Context;
import androway.common.Exceptions.NotSupportedQueryTypeException;
import androway.connection.ConnectionManager;
import java.util.ArrayList;

/**
 * Class HttpManager stores log data on the androway.nl domain.
 * @author Rinse
 * @since 10-03-2011
 * @version 0.4
 */
public class HttpManager implements DatabaseManager {
	ConnectionManager _httpManager;
	String _dbName;

	public HttpManager(Context context) {
		_httpManager = new androway.connection.HttpManager();
		_dbName = "logs";
	}

	public void executeNonQuery(String query) throws NotSupportedQueryTypeException {
		_httpManager.post("executeNonQuery", _dbName, query);
	}

	public ArrayList<ArrayList<String>> getData(String query) {
		return _httpManager.get("getData", _dbName, query);
	}
}