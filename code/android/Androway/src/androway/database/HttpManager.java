package androway.database;

import android.content.Context;
import androway.common.Exceptions.NotSupportedQueryTypeException;
import java.util.Map;

/**
 * Class HttpManager stores log data on the androway.nl domain.
 * @author Rinse
 * @since 17-02-2011
 * @version 0.3
 */
public class HttpManager implements DatabaseManager {
	public HttpManager(Context context) {}

	public void executeNonQuery(String query) throws NotSupportedQueryTypeException {
		
	}

	public Map getData(String query) {
		return null;
	}
}