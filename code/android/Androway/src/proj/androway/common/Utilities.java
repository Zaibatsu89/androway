package proj.androway.common;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Utilities class contains common utilities that are used throughout the whole application
 * @author Rinse Cramer & Tymen Steur
 * @since 06-06-2011
 * @version 0.5
 */
public class Utilities
{
    /**
     * Encode the given string using the unique device id
     * @param toEncode  The string to encode
     * @return The encoded string
     */
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

    /**
     * Decode the given string using the unique device id
     * @param toDecode  The string to decode
     * @return The decoded string
     */
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