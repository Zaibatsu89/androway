package proj.androway.database;

import android.content.Context;
import proj.androway.common.SharedObjects;

/**
 * DatabaseManagerBase is an extension of the IDatabaseManager interface.
 * Protected methods that are mandatory for the DatabaseManagers must be declared here.
 * @author Tymen
 * @since 17-03-2011
 * @version 0.1
 */
public abstract class DatabaseManagerBase implements IDatabaseManager
{
	// All constants for the DatabaseManagers
	public static final String TYPE_LOCAL = "local";
	public static final String TYPE_HTTP = "http";

        protected SharedObjects _sharedObjects;
        protected Context _context;

        public DatabaseManagerBase(SharedObjects sharedObjects, Context context)
        {
            _sharedObjects = sharedObjects;
            _context = context;
        }

	// Should be all protected accessible methods (so not accessible from the outside)
	// protected abstract void methodName();
}
