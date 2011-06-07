package proj.androway.connection.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.text.TextUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import org.apache.http.NameValuePair;
import proj.androway.common.Constants;
import proj.androway.connection.ConnectionManagerBase;

/**
 * The BluetoothManager class sets up the bluetooth connection
 * between the Android device and the Androway.
 * @author Rinse Cramer & Tymen Steur
 * @since 06-06-2011
 * @version 0.5
 */
public class BluetoothManager extends ConnectionManagerBase
{
    private final UUID _btUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothAdapter _adapter;
    private ConnectedThread _connectedThread;
    private boolean _connected = false;
    private ReceivedDataListener ListenerEvent;

    /**
     * The constructor for the BluetoothManager
     * @param context   The application context
     */
    public BluetoothManager(Context context)
    {
        super(context);
    }

    /**
     * This function is not supported for the BluetoothManager.
     * Use the single paramter version instead.
     */
    public boolean open(String address, ArrayList<NameValuePair> data)
    {
        throw new UnsupportedOperationException("Not supported for the BluetoothManager.");
    }

    /**
     * Open (start) the bluetooth connection with the device using the given address
     * @param address   The bluetooth address
     * @return Whether the connection was succesfully established or not
     */
    @Override
    public boolean open(String address)
    {
        boolean result = false;
        address = address.toUpperCase();
        
        // Get the bluetooth adapter
        _adapter = BluetoothAdapter.getDefaultAdapter();

        // If no bluetooth address is given, opening a connection is impossible. Don't proceed.
        // If the adapter is null, then Bluetooth is not supported. Don't proceed.
        if(!TextUtils.isEmpty(address) && _adapter != null)
        {
            if (_adapter.isEnabled())
            {
                // Bluetooth is enabled, so connect to the device with the given address
                return connect(address);
            }
        }

        return result;
    }

    /**
     * Try to connect to the bluetooth device with the given address
     * @param address   The address of the bluetooth device to connect to
     * @return Whether the connection succeeded or not
     */
    private boolean connect(String address)
    {
        // Cancel any thread currently running a connection
        if (_connectedThread != null) {_connectedThread.close(); _connectedThread = null;}

        // Get the device and cancel discovery because it will slow down the connection
        BluetoothDevice device = _adapter.getRemoteDevice(address);
        _adapter.cancelDiscovery();

        // Get a BluetoothSocket for a connection with the BluetoothDevice
        BluetoothSocket socket = null;
        try
        {
            socket = device.createRfcommSocketToServiceRecord(_btUUID);
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
        if (_connectedThread != null) {_connectedThread.close(); _connectedThread = null;}

        // Start the thread to manage the connection and perform transmissions
        _connected = true;
        _connectedThread = new ConnectedThread(socket);
        _connectedThread.start();

        return true;
    }

    /**
     * This function is not supported for the BluetoothManager.
     * Use the zero paramter version instead.
     */
    public void close(String address)
    {
        throw new UnsupportedOperationException("Not supported for the BluetoothManager.");
    }

    /**
     * Close the current bluetooth connection
     */
    public void close()
    {
        if (_connectedThread != null)
        {
            _connectedThread.close();
            _connectedThread = null;
        }
    }

    /**
     * Check the bluetooth connection
     * @return Whether there is a connection or not
     */
    public boolean checkConnection()
    {
        return _connected;
    }

    /**
     * Post (send) the given data to the connected bluetooth device
     * @param address   Can be an empty string, not used for the BluetoothManager
     * @param data      The data to send
     * @return Whether the post was succesful or not
     */
    public boolean post(String address, ArrayList<NameValuePair> data)
    {
        boolean result = false;

        // First check if we have a connection
        if(checkConnection())
        {
            String valuesToSend = "";

            // Append the given values to the valuesToSend string
            for(NameValuePair nameValuePair : data)
                valuesToSend += nameValuePair.getValue();

            // Add the verification string and message separator to the data
            valuesToSend = Constants.BT_VERIFICATION_STRING + valuesToSend + Constants.BT_MESSAGE_SEPARATOR;

            // Write the data to the connect thread (send through bluetooth)
            _connectedThread.write(valuesToSend.getBytes());

            result = true;
        }

        return result;
    }

    /**
     * This function is not supported for the BluetoothManager.
     */
    public Map<String, Object> get(String address, ArrayList<NameValuePair> params)
    {
        throw new UnsupportedOperationException("Not supported for the BluetoothManager.");
    }

    /**
     * Register the given bluetooth received listener (callback)
     * @param receivedDataListener The bluetooth received listener (callback)
     */
    public void onBluetoothReceivedData(ReceivedDataListener receivedDataListener)
    {
        ListenerEvent = receivedDataListener;
    }

    /**
     * The bluetooth received event
     */
    public interface ReceivedDataListener
    {
        /**
         * Handle the received data
         * @param data  The received data
         */
        public abstract void handleData (ArrayList data);
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
            int verificationCounter = 0;
            boolean messageVerified = false;
            ArrayList values = new ArrayList();
            String value = "";

            // Keep listening to the InputStream while connected
            while (true)
            {
                try
                {
                    // Read from the InputStream
                    bytes = _inStream.read(buffer);

                    // Construct a string from the valid bytes in the buffer
                    String messageString = new String(buffer, 0, bytes);

                    // Loop the received message string
                    for(char c : messageString.toCharArray())
                    {
                        if(!messageVerified && c == Constants.BT_VERIFICATION_STRING.charAt(verificationCounter))
                        {
                            verificationCounter ++;

                            if(verificationCounter == Constants.BT_VERIFICATION_STRING.length())
                                messageVerified = true;
                        }
                        else if(messageVerified)
                        {
                            if(c == Constants.BT_VALUE_SEPARATOR || c == Constants.BT_MESSAGE_SEPARATOR)
                            {
                                // We reached either value- or message-separator, so the value is done. Store the value
                                // in the values list, and reset the value.
                                values.add(value);
                                value = "";
                            }

                            if(c == Constants.BT_MESSAGE_SEPARATOR)
                            {
                                // We've hit the message separator, so end of message. Handle it.
                                if(ListenerEvent != null)
                                    ListenerEvent.handleData(values);

                                // When the message is done, reset the process variables for the next message
                                verificationCounter = 0;
                                messageVerified = false;
                                values = new ArrayList();
                                value = "";
                            }                            
                            else if(c != Constants.BT_VALUE_SEPARATOR)
                                value += c;
                        }
                    }
                }
                catch (IOException e)
                {
                    // The connection was lost
                    _connected = false;
                    break;
                }
            }
        }

        /**
         * Write (send) the given buffer to the OutputStream
         * @param buffer
         */
        public void write(byte[] buffer)
        {
            try { _outStream.write(buffer); } catch (IOException e) { }
        }

        /**
         * Close the connection
         */
        public void close()
        {
            try { _socket.close(); } catch (IOException e) { }
        }
    }
}