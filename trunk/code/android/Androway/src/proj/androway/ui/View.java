package proj.androway.ui;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import proj.androway.R;
import proj.androway.common.Settings;
import proj.androway.common.SharedObjects;
import proj.androway.main.ActivityBase;

/**
 * The View class is the main (default) view of the application.
 * @author Rinse Cramer & Tymen Steur
 * @since 06-06-2011
 * @version 0.5
 */
public class View extends ActivityBase
{
    private SharedObjects _sharedObjects;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.main);

        _sharedObjects = (SharedObjects)this.getApplication();

        this.showDialog(0);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        // If the language has changed, restart this activity so it will show the new language
        if(Settings.LANGUAGE_CHANGED)
        {
            startActivity(new Intent(this.getBaseContext(), View.class));
            Settings.LANGUAGE_CHANGED = false;
        }

        this.showDialog(0);
    }

    @Override
    protected Dialog onCreateDialog(int dialogId)
    {
        // Create a new dialog with the correct layout
        Dialog dialog = new Dialog(View.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.view_dialog);
        dialog.setCancelable(false);

        // The 'view old session' layout and icon button with their onclick listeners
        LinearLayout viewOldSessionButton = (LinearLayout) dialog.findViewById(R.id.view_old_session);
        ImageButton viewOldSessionIcon = (ImageButton) viewOldSessionButton.findViewById(R.id.view_old_session_icon);
        viewOldSessionButton.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener()
        {
            public void onFocusChange(android.view.View v, boolean hasFocus){((ImageButton)v.findViewById(R.id.view_old_session_icon)).requestFocus();}
        });
        android.view.View.OnClickListener viewOldSessionClick = new android.view.View.OnClickListener()
        {
            public void onClick(android.view.View v){startActivity(new Intent(v.getContext(), ViewOldSessionView.class));}
        };
        viewOldSessionIcon.setOnClickListener(viewOldSessionClick);
        viewOldSessionButton.setOnClickListener(viewOldSessionClick);

        // The 'view settings' layout and icon button with their onclick listeners
        LinearLayout viewSettingsButton = (LinearLayout) dialog.findViewById(R.id.view_settings);
        ImageButton viewSettingsIcon = (ImageButton) viewSettingsButton.findViewById(R.id.view_settings_icon);
        viewSettingsButton.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener()
        {
            public void onFocusChange(android.view.View v, boolean hasFocus){((ImageButton)v.findViewById(R.id.view_settings_icon)).requestFocus();}
        });
        android.view.View.OnClickListener viewSettingsClick = new android.view.View.OnClickListener()
        {
            public void onClick(android.view.View v){startActivity(new Intent(v.getContext(), SettingsView.class));}
        };
        viewSettingsIcon.setOnClickListener(viewSettingsClick);
        viewSettingsButton.setOnClickListener(viewSettingsClick);

        // The 'start new session' layout and icon button with their onclick listeners
        LinearLayout newSessionButton = (LinearLayout) dialog.findViewById(R.id.start_session);
        ImageButton newSessionIcon = (ImageButton) newSessionButton.findViewById(R.id.start_session_icon);
        newSessionButton.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener()
        {
            public void onFocusChange(android.view.View v, boolean hasFocus){((ImageButton)v.findViewById(R.id.start_session_icon)).requestFocus();}
        });
        android.view.View.OnClickListener newSessionClick = new android.view.View.OnClickListener()
        {
            public void onClick(android.view.View v)
            {
                _sharedObjects.controller.runSession();
            }
        };
        newSessionIcon.setOnClickListener(newSessionClick);
        newSessionButton.setOnClickListener(newSessionClick);

        // If the session is running change the text for the button. When clicked
        // the start session is called, but this is correctly handled from the controller.
        if(Settings.SESSION_RUNNING)
        {
            TextView sessionText = (TextView) dialog.findViewById(R.id.start_session_text);
            sessionText.setText(R.string.show_running_session);
        }

        return dialog;
    }
}