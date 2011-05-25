package proj.androway.connection;

import android.content.Context;
import java.util.ArrayList;
import org.apache.http.NameValuePair;
import proj.androway.common.SharedObjects;

/**
 * ConnectionManagerBase is an extension of the IConnectionManager interface.
 * Protected methods that are mandatory for the ConnectionManagers must be declared here.
 * @author Tymen
 * @since 17-03-2011
 * @version 0.1
 */
public abstract class ConnectionManagerBase implements IConnectionManager
{
    // All constants for the ConnectionManagers
    public static final String TYPE_BLUETOOTH = "bluetooth";
    public static final String TYPE_HTTP = "http";
    
    protected SharedObjects _sharedObjects;
    protected Context _context;

    public ConnectionManagerBase(SharedObjects sharedObjects, Context context)
    {
        _sharedObjects = sharedObjects;
        _context = context;
    }

    // Abstract methods should be all protected accessible methods (so not accessible from the outside)
    // protected abstract void methodName();

    public boolean open(String address)
    {
        return open(address, new ArrayList<NameValuePair>());
    }
}
