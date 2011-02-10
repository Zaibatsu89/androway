/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package androway.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;
import androway.quick_action.ActionItem;
import androway.quick_action.QuickAction;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Tymen
 */
public class Main extends Activity
{
    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);

        // Set the content of our screen
        setContentView(R.layout.main);

        final List<ActionItem> actionItems = new ArrayList<ActionItem>();

        ActionItem connect = new ActionItem();
        connect.setTitle("Connect");
        connect.setIcon(getResources().getDrawable(R.drawable.bt_connect_icon));
        connect.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {
                Toast.makeText(Main.this, "Connecting to the Segway" , Toast.LENGTH_SHORT).show();
            }
        });
        actionItems.add(connect);

        ActionItem disconnect = new ActionItem();
        disconnect.setTitle("Disconnect");
        disconnect.setIcon(getResources().getDrawable(R.drawable.bt_disconnect_icon));
        disconnect.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {
                Toast.makeText(Main.this, "Disconnecting from the Segway", Toast.LENGTH_SHORT).show();
            }
        });
        actionItems.add(disconnect);

        ActionItem settings = new ActionItem();
        settings.setTitle("Settings");
        settings.setIcon(getResources().getDrawable(R.drawable.settings_icon));
        settings.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {
                Toast.makeText(Main.this, "Go to Bluetooth settings", Toast.LENGTH_SHORT).show();
            }
        });
        actionItems.add(settings);

        ImageButton btButton = (ImageButton) this.findViewById(R.id.bluetooth_button);
        btButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                QuickAction quickAction = new QuickAction(v);
                quickAction.setAnimStyle(QuickAction.ANIM_AUTO);

                // Bind the action items to the QuickAction
                for(ActionItem actionItem : actionItems)
                    quickAction.addActionItem(actionItem);

                quickAction.show();
            }
        });
    }
}