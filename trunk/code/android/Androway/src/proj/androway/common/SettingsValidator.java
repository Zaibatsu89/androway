package proj.androway.common;

import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SettingsValidator
{
    public static boolean validateBluetoothAddress(String bluetoothAddress)
    {
        boolean valid = false;

        Pattern pattern = Pattern.compile("^([0-9a-fA-F][0-9a-fA-F]:){5}([0-9a-fA-F][0-9a-fA-F])$");

        if(bluetoothAddress.length() == 17 && pattern.matcher(bluetoothAddress).matches())
            valid = true;

        return valid;
    }

    public static boolean validateEmail(String email)
    {
        Pattern pattern = Pattern.compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");        
        return pattern.matcher(email).matches();
    }

    public static boolean validatePassword(String password)
    {
        boolean valid = false;

        if(password.length() >= 6)
            valid = true;

        return valid;
    }
}
