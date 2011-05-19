package proj.androway.connection;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
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
    //common machine UUID that we need to communicate with Bluetooth module: 00001101-0000-1000-8000-00805F9B34FB
    private static final UUID MY_UUID_SECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String VERIFICATION_STRING = "$ANDROWAY001";
    private static final String MESSAGE_SEPARATOR = "~";

    private BluetoothAdapter _adapter;
    private ConnectedThread _connectedThread;
    private boolean _connected = false;

    public synchronized void run()
    {
    }

    public synchronized boolean open(String address, ArrayList<NameValuePair> data)
    {
        boolean result = false;
        address = address.toUpperCase();
        
        // Get the bluetooth adapter
        _adapter = BluetoothAdapter.getDefaultAdapter();

        // If no bluetooth address is given opening a connection is impossible. Don't proceed.
        // If the adapter is null, then Bluetooth is not supported. Don't proceed.
        if(!address.isEmpty() && _adapter != null)
        {
            if (_adapter.isEnabled())
            {
                // Bluetooth is enabled, so connect to the device with the given address
                return this.connect(address);
            }
        }

        return result;
    }

    /**
     * Try to connect to the bluetooth device with the given address
     * @param address   The address of the bluetooth device to connect to
     * @return Whether the connection succeeded or not
     */
    public boolean connect(String address)
    {
        // Cancel any thread currently running a connection
        if (_connectedThread != null) {_connectedThread.cancel(); _connectedThread = null;}

        // Get the device and cancel discovery because it will slow down the connection
        BluetoothDevice device = _adapter.getRemoteDevice(address);
        _adapter.cancelDiscovery();

        // Get a BluetoothSocket for a connection with the BluetoothDevice
        BluetoothSocket socket = null;
        try
        {
            socket = device.createRfcommSocketToServiceRecord(MY_UUID_SECURE);
        }
        catch (IOException e) { }

        // Make a connection to the BluetoothSocket.
        // This is a blocking call and will only return on a successful connection or an exception
        try
        {
            socket.connect();
        }
        catch (IOException e)
        {
            // Connecting failed, close the socket
            try { socket.close(); } catch (IOException e2) { }

            // Return false because the connecting failed
            return false;
        }

        // The connecting succeeded so start the connected thread
        // Start the connected thread and return true, because we are connected
        if (_connectedThread != null) {_connectedThread.cancel(); _connectedThread = null;}

        // Start the thread to manage the connection and perform transmissions
        _connected = true;
        _connectedThread = new ConnectedThread(socket);
        _connectedThread.start();

        return true;
    }

    public synchronized void close(String address)
    {
        if (_connectedThread != null)
        {
            _connectedThread.cancel();
            _connectedThread = null;
        }
    }

    public synchronized boolean checkConnection()
    {
        return _connected;
    }

    public synchronized boolean post(String address, ArrayList<NameValuePair> data)
    {
        String valuesToSend = "";

        // Append the given values to the valuesToSend string
        for(NameValuePair nameValuePair : data)
            valuesToSend += nameValuePair.getValue();

        // Add the verification string and message separator to the data
        valuesToSend = VERIFICATION_STRING + valuesToSend + MESSAGE_SEPARATOR;

        // Write the data to the connect thread (send through bluetooth)
        _connectedThread.write(valuesToSend.getBytes());

        return true;
    }

    public synchronized Map<String, Object> get(String address, ArrayList<NameValuePair> params)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread
    {
        private final BluetoothSocket _socket;
        private final InputStream _inStream;
        private final OutputStream _outStream;

        public ConnectedThread(BluetoothSocket socket)
        {
            _socket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try
            {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            }
            catch (IOException e) { }

            _inStream = tmpIn;
            _outStream = tmpOut;
        }

        @Override
        public void run()
        {
            byte[] buffer = new byte[1024];
            int bytes;

            // Keep listening to the InputStream while connected
            while (true)
            {
                try
                {
                    // Read from the InputStream
                    bytes = _inStream.read(buffer);

                    // Send the obtained bytes to the UI Activity
                    //mHandler.obtainMessage(AndrowayControl.MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                }
                catch (IOException e)
                {
                    // The connection was lost
                    _connected = false;
                    break;
                }
            }
        }

        public void write(byte[] buffer)
        {
            try { _outStream.write(buffer); } catch (IOException e) { }
        }

        public void cancel()
        {
            try { _socket.close(); } catch (IOException e) { }
        }
    }
}