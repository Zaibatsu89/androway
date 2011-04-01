package proj.androway.main;

import android.content.Context;
import android.widget.Toast;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import proj.androway.R;
import proj.androway.common.Exceptions.MapIsEmptyException;
import proj.androway.common.Exceptions.MaxPoolSizeReachedException;
import proj.androway.common.Exceptions.NotSupportedQueryTypeException;
import proj.androway.common.Settings;
import proj.androway.common.SharedObjects;
import proj.androway.logging.LoggingManager;
import proj.androway.ui.View;

/**
 *
 * @author Tymen
 */
public class Session
{
    private SharedObjects _sharedObjects;
    private LoggingManager _lm;
    private Context _context;

    public Session(SharedObjects sharedObjects, Context context)
    {
        _sharedObjects = sharedObjects;
        _context = context;
    }
    
    public void start()
    {
        // Create the logging manager
        try
        {
            _lm = new LoggingManager(_context, Settings.LOG_TYPE);
        }
        catch (MaxPoolSizeReachedException ex)
        {
            Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
        }

        Settings.putSetting("sessionRunning", true);
    }

    public void stop()
    {
        Settings.putSetting("sessionRunning", false);
    }

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
