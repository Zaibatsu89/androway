package proj.androway.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import proj.androway.R;
import proj.androway.common.Settings;
import proj.androway.common.SettingsValidator;
import proj.androway.common.Utilities;

public class SettingsView extends PreferenceActivity implements OnPreferenceChangeListener
{
    private Preference _encryptedPasswordPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        this.addPreferencesFromResource(R.xml.settings);
        this.getListView().setBackgroundColor(Color.parseColor("#b4000000"));      

        Editor e = ((Preference) findPreference("encryptedPassword")).getEditor();
        e.putString("userPassword", Settings.USER_PASSWORD);
        e.commit();

        // Remove the encrypted password from the UI.
        _encryptedPasswordPreference = (Preference) findPreference("encryptedPassword");
        this.getPreferenceScreen().removePreference(_encryptedPasswordPreference);

        // Set the change listener for the password field
        Preference emailPref = (Preference)findPreference("userEmail");
        Preference passwordPref = (Preference)findPreference("userPassword");
        Preference bluetoothPref = (Preference)findPreference("bluetoothAddress");
        
        emailPref.setOnPreferenceChangeListener(this);
        passwordPref.setOnPreferenceChangeListener(this);
        bluetoothPref.setOnPreferenceChangeListener(this);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue)
    {
        boolean result = false;
        String key = preference.getKey();

        if(key.equals("userEmail"))
        {
            if(!SettingsValidator.validateEmail((String)newValue))
                this.showInvalidAlert(R.string.emailTitle, R.string.emailInvalid);
            else
                result = true;
        }
        else if(key.equals("userPassword"))
        {
            if(SettingsValidator.validatePassword((String)newValue))
            {
                Editor e = _encryptedPasswordPreference.getEditor();
                e.putString("encryptedPassword", Utilities.encodeString((String)newValue));
                e.commit();
            }
            else
                this.showInvalidAlert(R.string.passwordTitle, R.string.passwordInvalid);
        }
        else if(key.equals("bluetoothAddress"))
        {
            if(!SettingsValidator.validateBluetoothAddress((String)newValue))
                this.showInvalidAlert(R.string.bluetoothTitle, R.string.bluetoothInvalid);
            else
                result = true;
        }

        return result;
    }

    private void showInvalidAlert(int titleId, int messageId)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(this.getString(titleId) + " " + this.getString(R.string.invalid));
        builder.setMessage(this.getString(messageId));
        builder.setCancelable(false);
        builder.setPositiveButton(this.getString(R.string.ok), new DialogInterface.OnClickListener()
        {
           public void onClick(DialogInterface dialog, int id){ dialog.cancel(); }
        });
        (builder.create()).show();
    }
}