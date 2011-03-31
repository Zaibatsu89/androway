package proj.androway.common;

import android.app.Application;
import proj.androway.main.Controller;
import proj.androway.main.Session;

/**
 *
 * @author Tymen
 */
public class SharedObjects extends Application
{
    public Controller controller;
    public Session session;

    public SharedObjects()
    {
    }
}
