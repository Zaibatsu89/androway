package proj.androway.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

/**
 * The SettingsView class is the view for the application settings
 * @author Rinse Cramer & Tymen Steur
 * @since 06-06-2011
 * @version 0.5
 */
public class SettingsView extends PreferenceActivity implements OnPreferenceChangeListener
{
    private Preference _encryptedPasswordPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        this.addPreferencesFromResource(R.xml.settings);
        this.getListView().setBackgroundColor(Color.parseColor("#b4000000"));

        // Put the encrypted password
        Editor e = ((Preference) findPreference("encryptedPassword")).getEditor();
        e.putString("userPassword", Settings.USER_PASSWORD);
        e.commit();

        // Load the most recent settings
        Settings.initSettings(this);

        // Remove the encrypted password from the UI.
        _encryptedPasswordPreference = (Preference) findPreference("encryptedPassword");
        this.getPreferenceScreen().removePreference(_encryptedPasswordPreference);

        // Set the change listener for the password field
        Preference emailPref = (Preference)findPreference("userEmail");
        Preference passwordPref = (Preference)findPreference("userPassword");
        Preference bluetoothPref = (Preference)findPreference("bluetoothAddress");
        Preference languagePref = (Preference)findPreference("appLanguage");

        // TEMP: allways blocked, because local logging is not implemented
        ((Preference)findPreference("httpLogging")).setEnabled(false);
        
        // If the session is currently running, block some settings. Otherwise bind change listeners.
        if(Settings.SESSION_RUNNING)
        {
            emailPref.setEnabled(false);
            passwordPref.setEnabled(false);
            bluetoothPref.setEnabled(false);
            languagePref.setEnabled(false);
            ((Preference)findPreference("httpLogging")).setEnabled(false);
            ((Preference)findPreference("deviceOrientation")).setEnabled(false);
        }
        else
        {
            emailPref.setOnPreferenceChangeListener(this);
            passwordPref.setOnPreferenceChangeListener(this);
            bluetoothPref.setOnPreferenceChangeListener(this);
            languagePref.setOnPreferenceChangeListener(this);
        }
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
                result = true;
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
        else if(key.equals("appLanguage"))
        {
            Settings.LANGUAGE_CHANGED = true;
            Settings.setLanguage(SettingsView.this, (String)newValue);
            startActivity(new Intent(this.getBaseContext(), SettingsView.class));

            result = true;
        }

        return result;
    }

    /**
     * Show an alert with the given title and message
     * @param titleId   The title resource id (R.string.your_title_id)
     * @param messageId The message resource id (R.string.your_message_id)
     */
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