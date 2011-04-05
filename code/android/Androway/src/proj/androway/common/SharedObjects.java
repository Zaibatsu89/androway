package proj.androway.common;

import android.app.Application;
import java.util.Map;
import java.util.Timer;
import proj.androway.main.Controller;
import proj.androway.ui.RunningSessionView;

/**
 *
 * @author Tymen
 */
public class SharedObjects extends Application
{
    public Controller controller;
    public RunningSessionView runningSessionView;
    public Timer updateTimer;
    public Map<String, Object> updatedData;

    public SharedObjects()
    {
    }
}
