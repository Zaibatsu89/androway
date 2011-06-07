package proj.androway.common;

import java.util.regex.Pattern;

/**
 * The SettingsValidator class contains all setting validation functions
 * @author Rinse Cramer & Tymen Steur
 * @since 06-06-2011
 * @version 0.5
 */
public class SettingsValidator
{
    /**
     * Validate the given bluetooth address
     * @param bluetoothAddress  The bluetooth address to validate
     * @return  Whether the given bluetooth address is valid or not
     */
    public static boolean validateBluetoothAddress(String bluetoothAddress)
    {
        boolean valid = false;

        Pattern pattern = Pattern.compile("^([0-9a-fA-F][0-9a-fA-F]:){5}([0-9a-fA-F][0-9a-fA-F])$");

        if(bluetoothAddress.length() == 17 && pattern.matcher(bluetoothAddress).matches())
            valid = true;

        return valid;
    }

    /**
     * Validate the given bluetooth address
     * @param email  The email address to validate
     * @return  Whether the given email address is valid or not
     */
    public static boolean validateEmail(String email)
    {
        Pattern pattern = Pattern.compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[_A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        return pattern.matcher(email).matches();
    }

    /**
     * Validate the given password
     * @param password  The password to validate
     * @return  Whether the given password is valid or not
     */
    public static boolean validatePassword(String password)
    {
        boolean valid = false;

        if(password.length() >= Constants.MINIMAL_PASSWORD_LENGTH)
            valid = true;

        return valid;
    }
}
