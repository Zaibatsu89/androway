package proj.androway.database;

import android.content.Context;

/**
 * DatabaseManagerBase is an extension of the IDatabaseManager interface.
 * Protected methods that are mandatory for the DatabaseManagers must be declared here.
 * @author Rinse Cramer & Tymen Steur
 * @since 06-06-2011
 * @version 0.5
 */
public abstract class DatabaseManagerBase implements IDatabaseManager
{
    // All constants for the DatabaseManagers
    public static final String TYPE_LOCAL = "local";
    public static final String TYPE_HTTP = "http";

    protected Context _context;

    public DatabaseManagerBase(Context context)
    {
        _context = context;
    }

    // Should be all protected accessible methods (so not accessible from the outside)
    // protected abstract void methodName();
}
