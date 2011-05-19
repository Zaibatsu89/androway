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

    // Member fields
    private final BluetoothAdapter _adapter;
    private ConnectThread _connectThread;
    private ConnectedThread _connectedThread;
    private int _connectionState;

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device

    public BluetoothManager()
    {
        _adapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (_adapter == null)
        {
            /*
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
            */

            System.out.println("Bluetooth is not available!!");
        }

        _connectionState = STATE_NONE;
    }

    /**
     * Set the current state of the chat connection
     * @param state  An integer defining the current connection state
     */
    private synchronized void setState(int state)
    {
        _connectionState = state;

        // Give the new state to the Handler so the UI Activity can update
        //mHandler.obtainMessage(AndrowayControl.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    /**
     * Return the current connection state. */
    public synchronized int getState()
    {
        return _connectionState;
    }

    /**
     * Start the chat service. Specifically start AcceptThread to begin a
     * session in listening (server) mode. Called by the Activity onResume() */
    public synchronized void start()
    {
        // Cancel any thread attempting to make a connection
        if (_connectThread != null) {_connectThread.cancel(); _connectThread = null;}

        // Cancel any thread currently running a connection
        if (_connectedThread != null) {_connectedThread.cancel(); _connectedThread = null;}
    }

    /**
     * Start the ConnectThread to initiate a connection to a remote device.
     * @param device  The BluetoothDevice to connect
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    public synchronized void connect(String address)
    {
        // Cancel any thread attempting to make a connection
        if (_connectionState == STATE_CONNECTING)
        {
            if (_connectThread != null) {_connectThread.cancel(); _connectThread = null;}
        }

        // Cancel any thread currently running a connection
        if (_connectedThread != null) {_connectedThread.cancel(); _connectedThread = null;}

        // Create a device with the given address and start the thread to connect with the device
        BluetoothDevice device = _adapter.getRemoteDevice(address);
        _connectThread = new ConnectThread(device);
        _connectThread.start();
        setState(STATE_CONNECTING);
    }

    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     * @param socket  The BluetoothSocket on which the connection was made
     * @param device  The BluetoothDevice that has been connected
     */
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device)
    {
        // Cancel the thread that completed the connection
        if (_connectThread != null) {_connectThread.cancel(); _connectThread = null;}

        // Cancel any thread currently running a connection
        if (_connectedThread != null) {_connectedThread.cancel(); _connectedThread = null;}

        // Start the thread to manage the connection and perform transmissions
        _connectedThread = new ConnectedThread(socket);
        _connectedThread.start();

        setState(STATE_CONNECTED);
    }

    /**
     * Stop all threads
     */
    public synchronized void stop()
    {
        if (_connectThread != null)
        {
            _connectThread.cancel();
            _connectThread = null;
        }

        if (_connectedThread != null)
        {
            _connectedThread.cancel();
            _connectedThread = null;
        }

        setState(STATE_NONE);
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner
     * @param out The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public void write(byte[] out)
    {
        // Create temporary object
        ConnectedThread r;
        
        // Synchronize a copy of the ConnectedThread
        synchronized (this)
        {
            if (_connectionState != STATE_CONNECTED) return;
            r = _connectedThread;
        }
        
        // Perform the write unsynchronized
        r.write(out);
    }

    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    private void connectionFailed()
    {
        // Send a failure message back to the Activity
        /*Message msg = mHandler.obtainMessage(AndrowayControl.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString("toast", "Unable to connect device");
        msg.setData(bundle);
        mHandler.sendMessage(msg);*/

        // Start the service over to restart listening mode
        this.start();
    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    private void connectionLost()
    {
        // Send a failure message back to the Activity
        /*
        Message msg = mHandler.obtainMessage(AndrowayControl.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString("toast", "Device connection was lost");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
        */

        // Start the service over to restart listening mode
        this.start();
    }

    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private class ConnectThread extends Thread
    {
        private final BluetoothSocket _socket;
        private final BluetoothDevice _device;

        public ConnectThread(BluetoothDevice device)
        {
            _device = device;
            BluetoothSocket tmp = null;

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try
            {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID_SECURE);
            }
            catch (IOException e) { }
            _socket = tmp;
        }

        @Override
        public void run()
        {
            setName("ConnectThread");

            // Always cancel discovery because it will slow down a connection
            _adapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try
            {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                _socket.connect();
            }
            catch (IOException e)
            {
                // Close the socket
                try
                {
                    _socket.close();
                }
                catch (IOException e2) { }


                System.out.println("FAILED..!!");
                

                connectionFailed();
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (BluetoothManager.this)
            {
                _connectThread = null;
            }


            System.out.println("CONNECTED..!!");


            // Start the connected thread
            connected(_socket, _device);
        }

        public void cancel()
        {
            try
            {
                _socket.close();
            }
            catch (IOException e) { }
        }
    }

    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread
    {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket)
        {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try
            {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            }
            catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
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
                    bytes = mmInStream.read(buffer);

                    // Send the obtained bytes to the UI Activity
                    //mHandler.obtainMessage(AndrowayControl.MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                }
                catch (IOException e)
                {
                    connectionLost();
                    break;
                }
            }
        }

        /**
         * Write to the connected OutStream.
         * @param buffer  The bytes to write
         */
        public void write(byte[] buffer)
        {
            try
            {
                mmOutStream.write(buffer);
            }
            catch (IOException e) { }
        }

        public void cancel()
        {
            try
            {
                mmSocket.close();
            }
            catch (IOException e) { }
        }
    }









    public synchronized void run()
    {
    }

    public synchronized boolean open(String address, ArrayList<NameValuePair> data)
    {
        boolean result = false;
        address = address.toUpperCase();
        
        if(!address.isEmpty())
        {
            // If BT is not on, request that it be enabled.
            // setupChat() will then be called during onActivityResult
            if (!_adapter.isEnabled())
                System.out.println("Bluetooth is turned off..!!");
            else
            {
                // Bluetooth is enabled, so connect to the device with the given address
                this.connect(address);
                result = true;
            }
        }

        return result;
    }

    public synchronized void close(String address)
    {
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