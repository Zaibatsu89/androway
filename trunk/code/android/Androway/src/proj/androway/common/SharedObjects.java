package proj.androway.common;

import android.app.Application;
import java.util.Timer;
import proj.androway.main.Controller;
import proj.androway.main.IncomingData;
import proj.androway.main.OutgoingData;
import proj.androway.session.Session;
import proj.androway.ui.RunningSessionView;

/**
 *
 * @author Tymen
 */
public class SharedObjects extends Application
{
    public Controller controller;
    public RunningSessionView runningSessionView;
    public Session session;
    public Timer updateTimer;
    public IncomingData incomingData;
    public OutgoingData outgoingData;
}
