package proj.androway.connection;

import android.content.Context;
import java.util.ArrayList;
import org.apache.http.NameValuePair;

/**
 * ConnectionManagerBase is an extension of the IConnectionManager interface.
 * Protected methods that are mandatory for the ConnectionManagers must be declared here.
 * @author Rinse Cramer & Tymen Steur
 * @since 06-06-2011
 * @version 0.5
 */
public abstract class ConnectionManagerBase implements IConnectionManager
{
    // All constants for the ConnectionManagers
    public static final String TYPE_BLUETOOTH = "bluetooth";
    public static final String TYPE_HTTP = "http";

    protected Context _context;

    public ConnectionManagerBase(Context context)
    {
        _context = context;
    }

    // Abstract methods should be all protected accessible methods (so not accessible from the outside)
    // protected abstract void methodName();

    public boolean open(String address)
    {
        return open(address, new ArrayList<NameValuePair>());
    }
}
