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
 * Class Settings stores and saves the settings.
 * @author Rinse
 * @since 17-03-2011
 * @version 0.11
 */
public class Settings
{
    public static String LOG_TYPE;
    public static String BLUETOOTH_ADDRESS;
    public static String APP_LANGUAGE;
    public static String USER_EMAIL;
    public static String USER_PASSWORD;
    public static String LAST_ACTIVITY;

    public static int DEVICE_ORIENTATION;

    public static boolean BLOCK_1_LOCKED;
    public static boolean BLOCK_2_LOCKED;
    public static boolean SESSION_RUNNING;

    public static boolean LANGUAGE_CHANGED = false;

    private static Context _context;

    public static void initSettings(Context context)
    {
        _context = context;
        
        // Get the xml/settings.xml preferences
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);

        BLOCK_1_LOCKED      = settings.getBoolean("block1Locked", false);
        BLOCK_2_LOCKED      = settings.getBoolean("block2Locked", false);
        SESSION_RUNNING     = settings.getBoolean("sessionRunning", false);
        BLUETOOTH_ADDRESS   = settings.getString("bluetoothAddress", "00:00:00:00:00:00");
        APP_LANGUAGE        = settings.getString("appLanguage", "gb");
        LAST_ACTIVITY       = settings.getString("lastActivity", View.class.getName());
        USER_EMAIL          = settings.getString("userEmail", "");
        USER_PASSWORD       = Utilities.decodeString(settings.getString("encryptedPassword", ""));

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

        setLanguage(context, APP_LANGUAGE);
    }

    /*
     * Change the system language
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

    public static void putSetting(String name, String value)
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(_context);

        Editor editor = settings.edit();
        editor.putString(name, value);
        editor.commit();
    }

    public static void putSetting(String name, int value)
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(_context);

        Editor editor = settings.edit();
        editor.putInt(name, value);
        editor.commit();
    }

    public static void putSetting(String name, float value)
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(_context);

        Editor editor = settings.edit();
        editor.putFloat(name, value);
        editor.commit();
    }

    public static void putSetting(String name, boolean value)
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(_context);

        Editor editor = settings.edit();
        editor.putBoolean(name, value);
        editor.commit();
    }
}