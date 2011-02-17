package androway.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import androway.logging.LocalManager;
import androway.logging.LoggingManager;
import androway.ui.quick_action.ActionItem;
import androway.ui.quick_action.QuickAction;
import java.util.ArrayList;
import java.util.List;

/**
 * Class View is stoer.
 * @author Tymen en Rinse
 * @since 17-02-2011
 * @version 0.3
 */
public class View extends Activity {
	LoggingManager lm;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.main);
		lm = new LocalManager(this);

        final List<ActionItem> actionItems = new ArrayList<ActionItem>();

        ActionItem connect = new ActionItem();
        //connect.setTitle(this.getString(R.string.connect));
		connect.setTitle(this.getString(R.string.add));
        connect.setIcon(getResources().getDrawable(R.drawable.bt_connect_icon));
        connect.setOnClickListener(new OnClickListener()
        {
            public void onClick(android.view.View v)
            {
                //Toast.makeText(View.this, "Connecting to the Segway", Toast.LENGTH_SHORT).show();
				lm.add("NHL Hogeschool", "Minor Androway");
            }
        });
        actionItems.add(connect);

        ActionItem disconnect = new ActionItem();
		//disconnect.setTitle(this.getString(R.string.disconnect));
		disconnect.setTitle(this.getString(R.string.get));
        disconnect.setIcon(getResources().getDrawable(R.drawable.bt_disconnect_icon));
        disconnect.setOnClickListener(new OnClickListener()
        {
            public void onClick(android.view.View v)
            {
                //Toast.makeText(View.this, "Disconnecting from the Segway", Toast.LENGTH_SHORT).show();
				lm.get();
            }
        });
        actionItems.add(disconnect);

        ActionItem settings = new ActionItem();
        //settings.setTitle(this.getString(R.string.settings));
		settings.setTitle(this.getString(R.string.remove));
        settings.setIcon(getResources().getDrawable(R.drawable.settings_icon));
        settings.setOnClickListener(new OnClickListener()
        {
            public void onClick(android.view.View v)
            {
                //Toast.makeText(View.this, "Go to Bluetooth settings", Toast.LENGTH_SHORT).show();
				lm.remove();
            }
        });
        actionItems.add(settings);

        ImageButton btButton = (ImageButton) this.findViewById(R.id.bluetooth_button);
        btButton.setOnClickListener(new android.view.View.OnClickListener()
        {
            public void onClick(android.view.View v)
            {
                QuickAction quickAction = new QuickAction(v);
                quickAction.setAnimStyle(QuickAction.ANIM_AUTO);

                // Bind the action items to the QuickAction
                for(ActionItem actionItem : actionItems)
                    quickAction.addActionItem(actionItem);

                quickAction.show();
            }
        });
		
        ImageButton logWebButton = (ImageButton) this.findViewById(R.id.log_web_button);
        logWebButton.setOnClickListener(new android.view.View.OnClickListener()
        {
            public void onClick(android.view.View v)
            {
   		Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.segway_body_inclination);

                // Getting width & height of the given image.
		int w = bmp.getWidth();
		int h = bmp.getHeight();

                // Setting post rotate to 90
		Matrix mtx = new Matrix();
		mtx.postRotate(45);

                // Rotating Bitmap
		Bitmap rotatedBMP = Bitmap.createBitmap(bmp, 0, 0, w, h, mtx, true);
		BitmapDrawable bmd = new BitmapDrawable(rotatedBMP);

                ImageView img = (ImageView)findViewById(R.id.segway_body);
		img.setImageDrawable(bmd);
            }
        });
    }
}