package proj.androway.common;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class Utilities is.
 * @author Rinse
 * @since 10-02-2011
 * @version 0.1
 */
public class Utilities
{
    public static String encodeString(String toEncode)
    {
        String result = null;
        
        try
        {
            result = SimpleCrypto.encrypt(Constants.CUSTOM_UNIQUE_ID, toEncode);
        }
        catch (Exception ex)
        {
            Logger.getLogger(Utilities.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }

    public static String decodeString(String toDecode)
    {
        String result = null;

        try
        {
            result = SimpleCrypto.decrypt(Constants.CUSTOM_UNIQUE_ID, toDecode);
        }
        catch (Exception ex)
        {
            Logger.getLogger(Utilities.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }
}