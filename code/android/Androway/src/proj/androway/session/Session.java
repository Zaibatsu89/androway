/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package proj.androway.session;

import android.content.Context;
import android.view.View;
import android.widget.Toast;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import proj.androway.R;
import proj.androway.common.Exceptions.ConstructingLoggingManagerFailedException;
import proj.androway.common.Exceptions.MapIsEmptyException;
import proj.androway.common.Exceptions.MaxPoolSizeReachedException;
import proj.androway.common.Exceptions.NotSupportedQueryTypeException;
import proj.androway.common.Settings;
import proj.androway.database.DatabaseManagerBase;
import proj.androway.logging.LoggingManager;
import proj.androway.ui.RunningSessionView;

/**
 *
 * @author Tymen
 */
public class Session implements Runnable
{
    public static final int MSG_SET_VIEW = 0;
    public static final int MSG_SET_VALUE = 1;
    public static final int MSG_UPDATE_DIALOG = 2;

    private Context _context;
    private boolean _running = true;
    private LoggingManager _lm;

    public Session(Context context)
    {
        _context = context;
    }

    public void run()
    {
        while(_running) { }
    }

    /*
     * The logic for starting the Androway session
     */
    public int[] startSession()
    {
        synchronized(this)
        {
            int successfullyStarted = 0;
            int dialogType = -1;
            int message = -1;

            // Create the logging manager
            try
            {
                _lm = new LoggingManager(_context, Settings.LOG_TYPE);

                // TEMPORARY, FAKE BLUETOOTH PROCESS
                if(Settings.LOG_TYPE.equals(DatabaseManagerBase.TYPE_LOCAL))
                {
                    long endTime = System.currentTimeMillis() + 3 * 1000;
                    while (System.currentTimeMillis() < endTime)
                    {
                        synchronized (this)
                        {
                            try
                            {
                                wait(endTime - System.currentTimeMillis());
                            } catch (Exception e) { }
                        }
                    }
                }

                // Set the dialog type to trigger when done
                dialogType = RunningSessionView.DIALOG_TYPE_DONE;

                // The session was successfully started
                successfullyStarted = 1;
            }
            catch (ConstructingLoggingManagerFailedException ex)
            {
                long endTime = System.currentTimeMillis() + 3 * 1000;
                while (System.currentTimeMillis() < endTime)
                {
                    synchronized (this)
                    {
                        try
                        {
                            wait(endTime - System.currentTimeMillis());
                        } catch (Exception e) { }
                    }
                }

                // Set the dialog type and its message to trigger when done
                dialogType = RunningSessionView.DIALOG_TYPE_FAILED;
                message = R.string.login_failed_message;
            }
            catch (MaxPoolSizeReachedException ex)
            {
                Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
            }

            Settings.putSetting("sessionRunning", true);

            // Return an int array with if the login was succesfull, the dialogType and the optional message
            return new int[]{successfullyStarted, dialogType, message};
        }
    }

    /*
     * The logic for stopping the session
     */
    public void stopSession()
    {
        synchronized(this)
        {
            // Set the session running setting to false
            Settings.putSetting("sessionRunning", false);
            _running = false;
        }
    }

// Three temporary log functions. Should be implemented when a new BT message is received.
public void tempAddLog()
{
    try
    {
        _lm.addLog("NHL Hogeschool", "Minor Androway");
    }
    catch (NotSupportedQueryTypeException ex)
    {
        Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
    }
}
public void tempGetLogs()
{
    Map<String, Object> dataMap = new HashMap<String, Object>();

    try
    {
        dataMap = _lm.getLogs();
    }
    catch (MapIsEmptyException ex)
    {
        Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
    }

    if(dataMap.isEmpty())
    {
        // Show empty toast
        Toast.makeText(_context, _context.getResources().getString(R.string.empty), Toast.LENGTH_LONG).show();
    }
    else
    {
        // Loop dataMap
        for (int i = 0; i < dataMap.size(); i++)
        {
            Map<String, Object> rowMap = (Map<String, Object>) dataMap.get("row" + i);

            Toast.makeText(_context,
            "id: " + rowMap.get("id") +
            "\ntime: " + rowMap.get("time") +
            "\nsubject: " + rowMap.get("subject") +
            "\nmessage: " + rowMap.get("message"),
            Toast.LENGTH_LONG).show();
        }
    }
}
public void tempClearLogs()
{
    try
    {
        _lm.clearAll();
    }
    catch (NotSupportedQueryTypeException ex)
    {
        Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
    }
}

}