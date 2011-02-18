package org.me.android_experiment_05;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rinse
 */
public class BluetoothManager extends Activity {
	// change this to your Bluetooth device _address
	private static final String DEVICE_ADDRESS =  "20:7C:8F:49:FD:5E";

	private EditText _btAddress;
	private String _address;

	private BluetoothAdapter _btA;
	private BluetoothDevice _btD;
	private BluetoothSocket _btS;

	private InputStream _inputStream;
	private OutputStream _outputStream;
	private int[] _read;
	private int[] _write;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        // ToDo add your GUI initialization code here
		setContentView(R.layout.main);

		_btA = BluetoothAdapter.getDefaultAdapter();

		_btAddress = (EditText) this.findViewById(R.id.bt_address);
		_btAddress.setText(DEVICE_ADDRESS);

		_read = new int[1];
		_write = new int[10];
		_write[0] = 0;
		_write[1] = 5;
		_write[2] = 1;
		_write[3] = 5;
		_write[4] = 4;
		_write[5] = 6;
		_write[6] = 9;
		_write[7] = 7;
		_write[8] = 8;
		_write[9] = 7;
		
		Button connectButton = (Button) this.findViewById(R.id.btn_connect);
        connectButton.setOnClickListener(new android.view.View.OnClickListener()
        {
            public void onClick(android.view.View v)
            {
				_address = _btAddress.getText().toString();
				try {
                if (BluetoothAdapter.checkBluetoothAddress(_address))
					_btD = _btA.getRemoteDevice(_address);
				else
					throw new Exception("Bluetooth adres is incorrect:\n" + _address);
				} catch (Exception ex) {
					Logger.getLogger(BluetoothManager.class.getName()).log(Level.SEVERE, null, ex);
				}
				
				Toast.makeText(BluetoothManager.this, "Vriendelijke naam:\n" + _btD.getName(), Toast.LENGTH_LONG).show();

				try {
					_btS = _btD.createRfcommSocketToServiceRecord(UUID.randomUUID());
				} catch (IOException ex) {
					Logger.getLogger(BluetoothManager.class.getName()).log(Level.SEVERE, null, ex);
				}

				try {
					_btS.connect();
				} catch (IOException ex) {
					Logger.getLogger(BluetoothManager.class.getName()).log(Level.SEVERE, null, ex);
				}

				try {
					_inputStream = _btS.getInputStream();
				} catch (IOException ex) {
					Logger.getLogger(BluetoothManager.class.getName()).log(Level.SEVERE, null, ex);
				}

				try {
					int i = 1;

					while (_inputStream.available() > 0)
					{
						_read[i] = _inputStream.read();
						i++;
					}

					_inputStream.close();
				} catch (IOException ex) {
					Logger.getLogger(BluetoothManager.class.getName()).log(Level.SEVERE, null, ex);
				}

				for (int i : _read)
					Toast.makeText(BluetoothManager.this, "Gelezen waarde:\n" + _read[i], Toast.LENGTH_LONG).show();

				try {
					_outputStream = _btS.getOutputStream();
				} catch (IOException ex) {
					Logger.getLogger(BluetoothManager.class.getName()).log(Level.SEVERE, null, ex);
				}

				try {
					for (int i : _write)
					{
						_outputStream.write(_write[i]);
						_outputStream.flush();
					}

					_outputStream.close();
				} catch (IOException ex) {
					Logger.getLogger(BluetoothManager.class.getName()).log(Level.SEVERE, null, ex);
				}

				for (int i : _write)
					Toast.makeText(BluetoothManager.this, "Geschreven waarde:\n" + _write[i], Toast.LENGTH_LONG).show();

				try {
					_btS.close();
				} catch (IOException ex) {
					Logger.getLogger(BluetoothManager.class.getName()).log(Level.SEVERE, null, ex);
				}
            }
        });
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
}