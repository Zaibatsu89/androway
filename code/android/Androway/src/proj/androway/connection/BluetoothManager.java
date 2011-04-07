package proj.androway.connection;

import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.NameValuePair;

/**
 * Class BluetoothManager sets up the bluetooth
 * connection between the Android device and the
 * mini Segway.
 * @author Rinse
 * @since 10-03-2011
 * @version 0.41
 */
public class BluetoothManager extends ConnectionManagerBase
{
    private boolean _running = true;

    public synchronized void run()
    {
        //while(_running) { }
    }

    public synchronized boolean open(String address, ArrayList<NameValuePair> data)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public synchronized void close(String address)
    {
        _running = false;
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public synchronized boolean checkConnection()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public synchronized boolean post(String address, ArrayList<NameValuePair> data)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public synchronized Map<String, Object> get(String address, ArrayList<NameValuePair> params)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}