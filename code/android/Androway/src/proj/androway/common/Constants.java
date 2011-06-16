package proj.androway.common;

import android.os.Build;

/**
 * The Constants class contains the commonly used constant values for the application
 * @author Rinse Cramer & Tymen Steur
 * @since 06-06-2011
 * @version 0.5
 */
public class Constants
{
    /**
     * The common used id for the notifications
     */
    public static final int NOTIFICATION_ID = 1;

    /**
     * The minimal length for the login password (for online logging)
     */
    public static final int MINIMAL_PASSWORD_LENGTH = 6;

    /**
     * The database name to store the logs in
     */
    public static final String LOG_DB_NAME = "androway_logging";

    /**
     * The database table to store the logs in
     */
    public static final String LOG_DB_TABLE = "logs";

    /**
     * The id key for the logs database table
     */
    public static final String LOG_DB_ID_KEY = "log_id";

    /**
     * The root url for the remote website
     */
    public static final String ROOT_URL = "http://www.androway.nl";

    /**
     * The url for the remote (logging) webservice
     */
    public static final String WEBSERVICE_URL = ROOT_URL + "/webservices/loggingService.php";

    /**
     * The url for the remote authentication service
     */
    public static final String AUTH_WEBSERVICE_URL = ROOT_URL + "/webservices/authService.php";

    /**
     * The url for the remote mobile website.
     * (Passing from_app as a 'get' parameter to signal that the mobile website is loaded from within the app)
     */
    public static final String WEB_VIEW_URL = ROOT_URL + "/?from_app";

    /**
     * The bluetooth verification string based on the AndrowayID
     */
    //public static final String BT_VERIFICATION_STRING = "$AWY00000001";
    public static final String BT_VERIFICATION_STRING = "$ANDROWAY001";

    /**
     * The separator character that is used between each bluetooth message
     */
    public static final char BT_MESSAGE_SEPARATOR = '~';

    /**
     * The separator character that is used between each value of a message
     */
    public static final char BT_VALUE_SEPARATOR = ',';

    /**
     * The update interval for sending a bluetooth message to the Androway
     */
    public static final int BT_UPDATE_INTERVAL = 800;

    /**
     * A generated unique id for the device based on some of the device its details.
     * The unique id is used for encoding and decoding purposes (security).
     */
    public static final String CUSTOM_UNIQUE_ID =  "35" + //we make this look like a valid IMEI
                            Build.BOARD.length()%10+ Build.BRAND.length()%10 +
                            Build.CPU_ABI.length()%10 + Build.DEVICE.length()%10 +
                            Build.DISPLAY.length()%10 + Build.HOST.length()%10 +
                            Build.ID.length()%10 + Build.MANUFACTURER.length()%10 +
                            Build.MODEL.length()%10 + Build.PRODUCT.length()%10 +
                            Build.TAGS.length()%10 + Build.TYPE.length()%10 +
                            Build.USER.length()%10;
}
