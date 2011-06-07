package proj.androway.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import java.util.Locale;
import proj.androway.database.DatabaseManagerBase;
import proj.androway.ui.View;

/**
 * The Settings class stores and saves all common used settings
 * @author Rinse Cramer & Tymen Steur
 * @since 06-06-2011
 * @version 0.5
 */
public class Settings
{
    /**
     * The type of logging. The value can be either
     * DatabaseManagerBase.TYPE_HTTP or DatabaseManagerBase.TYPE_LOCAL.
     */
    public static String LOG_TYPE;

    /**
     * The bluetooth address to connect to
     */
    public static String BLUETOOTH_ADDRESS;

    /**
     * The active language for the application
     */
    public static String APP_LANGUAGE;

    /**
     * The users email address used for the remote login account.
     * Only needed when LOG_TYPE is set to DatabaseManagerBase.TYPE_HTTP
     */
    public static String USER_EMAIL;

    /**
     * The users password used for the remote login account.
     * Only needed when LOG_TYPE is set to DatabaseManagerBase.TYPE_HTTP
     */
    public static String USER_PASSWORD;

    /**
     * The last visited activity in this application.
     * Only used by the application and will not be touched by the user.
     */
    public static String LAST_ACTIVITY;

    /**
     * The desired device orientation, used for the RunningSessionView.
     * The value can be either ActivityInfo.SCREEN_ORIENTATION_PORTRAIT or ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
     */
    public static int DEVICE_ORIENTATION;

    /**
     * The balance control offset (the offset for the zero speed position).
     * The value must be >= 0 and <= 100
     */
    public static int BALANCE_CONTROL_OFFSET;

    /**
     * The balance control sensitivity.
     * The extra balance sensitiviy used when controlling the Androway.
     * The value must be >= 0 and <= 100
     */
    public static float BALANCE_CONTROL_SENSITIVITY;

    /**
     * Whether the first blocked is locked or not
     */
    public static boolean BLOCK_1_LOCKED;

    /**
     * Whether the second blocked is locked or not
     */
    public static boolean BLOCK_2_LOCKED;

    /**
     * A flag used by the system to signal whether the session is running or not
     */
    public static boolean SESSION_RUNNING;

    /**
     * A flag used by the system to signal whether the APP_LANGUAGE changed or not.
     * Used by the system to check if a UI refresh is required.
     */
    public static boolean LANGUAGE_CHANGED = false;

    /**
     * A flag used by the system to check whether the starting of the session failed or not.
     */
    public static boolean START_SESSION_FAILED = false;

    // The application context used to get the SharedPreferences
    private static Context _context;

    /**
     * Initialize (reload) the application settings
     * @param context   The application context
     */
    public static void initSettings(Context context)
    {
        _context = context;
        
        // Get the xml/settings.xml preferences
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(_context);

        // Get the settings values from the SharedPreferences and set a default value.
        BLOCK_1_LOCKED      = settings.getBoolean("block1Locked", false);
        BLOCK_2_LOCKED      = settings.getBoolean("block2Locked", false);
        SESSION_RUNNING     = settings.getBoolean("sessionRunning", false);
        BLUETOOTH_ADDRESS   = settings.getString("bluetoothAddress", "00:00:00:00:00:00");
        APP_LANGUAGE        = settings.getString("appLanguage", "gb");
        LAST_ACTIVITY       = settings.getString("lastActivity", View.class.getName());
        USER_EMAIL          = settings.getString("userEmail", "");
        USER_PASSWORD       = Utilities.decodeString(settings.getString("encryptedPassword", ""));

        // MOVE THE CALCULATION TO THE CLASS, HERE PURE VALUE FROM 0-100 SHOULD BE STORED
        BALANCE_CONTROL_OFFSET  = (int)(settings.getInt("1", 60) * 0.8f);
        BALANCE_CONTROL_SENSITIVITY = ((settings.getInt("2", 35) * 1.5f) / 100f) + 1;

        // Set the DEVICE_ORIENTATION based on the orientation setting
        String deviceOrientation = settings.getString("deviceOrientation", "portrait");
        if(deviceOrientation.equals("portrait"))
            DEVICE_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        else
            DEVICE_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;

        // Set the LOG_TYPE based on the httpLogging setting
        boolean httpLogging = settings.getBoolean("httpLogging", true);
        if(httpLogging)
            LOG_TYPE = DatabaseManagerBase.TYPE_HTTP;
        else
            LOG_TYPE = DatabaseManagerBase.TYPE_LOCAL;

        // Set the application language
        setLanguage(_context, APP_LANGUAGE);
    }

    /**
     * Change the application language
     * @param context   The application context
     * @param language  The new language to use
     */
    public static void setLanguage(Context context, String language)
    {
        Locale local = new Locale(language);
        Locale.setDefault(local);

        Configuration config = new Configuration();
        config.locale = local;

        Resources resources = context.getResources();
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    /**
     * Set the desired setting based on the name and the value
     * @param name  The key name of the setting
     * @param value The new value for the setting
     */
    public static void putSetting(String name, String value)
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(_context);

        Editor editor = settings.edit();
        editor.putString(name, value);
        editor.commit();
    }

    /**
     * Set the desired setting based on the name and the value
     * @param name  The key name of the setting
     * @param value The new value for the setting
     */
    public static void putSetting(String name, int value)
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(_context);

        Editor editor = settings.edit();
        editor.putInt(name, value);
        editor.commit();
    }

    /**
     * Set the desired setting based on the name and the value
     * @param name  The key name of the setting
     * @param value The new value for the setting
     */
    public static void putSetting(String name, float value)
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(_context);

        Editor editor = settings.edit();
        editor.putFloat(name, value);
        editor.commit();
    }

    /**
     * Set the desired setting based on the name and the value
     * @param name  The key name of the setting
     * @param value The new value for the setting
     */
    public static void putSetting(String name, boolean value)
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(_context);

        Editor editor = settings.edit();
        editor.putBoolean(name, value);
        editor.commit();
    }
}