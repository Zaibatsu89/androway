package proj.androway.common;

import android.os.Build;

/**
 * Class Constants stores and saves the used constants.
 * @author Rinse
 * @since 17-03-2011
 * @version 0.12
 */
public class Constants
{
    public static final int NOTIFICATION_ID = 1;

    public static final int MINIMAL_PASSWORD_LENGTH = 6;
    public static final String DATABASE_NAME = "androway_logging";
    public static final String DATABASE_TABLE = "logs";
    public static final String ROOT_URL = "http://www.androway.nl";
    public static final String WEBSERVICE_URL = ROOT_URL + "/webservices/loggingService.php";
    public static final String AUTH_WEBSERVICE_URL = ROOT_URL + "/webservices/authService.php";
    public static final String WEB_VIEW_URL = "http://www.androway.nl/site_index.php?from_app";

    // A unique id for the device
    public static final String CUSTOM_UNIQUE_ID =  "35" + //we make this look like a valid IMEI
                            Build.BOARD.length()%10+ Build.BRAND.length()%10 +
                            Build.CPU_ABI.length()%10 + Build.DEVICE.length()%10 +
                            Build.DISPLAY.length()%10 + Build.HOST.length()%10 +
                            Build.ID.length()%10 + Build.MANUFACTURER.length()%10 +
                            Build.MODEL.length()%10 + Build.PRODUCT.length()%10 +
                            Build.TAGS.length()%10 + Build.TYPE.length()%10 +
                            Build.USER.length()%10;
}
