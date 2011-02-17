package androway.connection;

import android.app.Activity;
import java.util.ArrayList;

/**
 * Class HttpManager sets up the http
 * connection between the Android phone
 * and the androway.nl domain.
 * @author Rinse
 * @since 17-02-2011
 * @version 0.3
 */
public class HttpManager implements ConnectionManager
{
	private Activity _mainActivity;

	public HttpManager(Activity mainActivity) {
		_mainActivity = mainActivity;
	}

	public void open() {

	}

	public void close() {

	}

	public void post() {

	}

	public ArrayList get() {
		return new ArrayList();
	}
}