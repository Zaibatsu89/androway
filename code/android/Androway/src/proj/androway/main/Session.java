package proj.androway.main;

import proj.androway.common.Settings;
import proj.androway.common.SharedObjects;

/**
 *
 * @author Tymen
 */
public class Session
{
    private SharedObjects _sharedObjects;

    public Session(SharedObjects sharedObjects)
    {
        _sharedObjects = sharedObjects;
    }
    
    public void start()
    {
        Settings.putSetting("sessionRunning", true);
    }

    public void stop()
    {
        Settings.putSetting("sessionRunning", false);
    }
}
