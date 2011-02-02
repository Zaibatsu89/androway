package org.me.android_experiment_01;

import android.app.Activity;
import android.os.Bundle;

public class Androway extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);
        
        // the content of our screen.
        setContentView(R.layout.main);
    }
}