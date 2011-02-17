package androway.logging;

import android.app.Activity;

/**
 * Class HttpManager stores log data on the androway.nl domain.
 * @author Rinse
 * @since 17-02-2011
 * @version 0.3
 */
public class HttpManager implements LoggingManager {
	private Activity _mainActivity;

	public HttpManager(Activity mainActivity) {
		_mainActivity = mainActivity;
	}


	public void add(String subject, String message) {

	}

	public void get() {

	}

	public void remove() {

	}
}