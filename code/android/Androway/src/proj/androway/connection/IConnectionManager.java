package proj.androway.connection;

import java.util.ArrayList;
import java.util.Map;
import org.apache.http.NameValuePair;

/**
 * IConnectionManager is the interface used to communicate with the different ConnectionManagers
 * @author Rinse
 * @since 17-03-2011
 * @version 0.43
 */
public interface IConnectionManager
{
	// Should be all public accessible methods
	public abstract boolean open(String address, ArrayList<NameValuePair> data);
	public abstract void close();
	public abstract boolean post(String address, ArrayList<NameValuePair> data);
	public abstract Map<String, Object> get(String address, ArrayList<NameValuePair> params);
}