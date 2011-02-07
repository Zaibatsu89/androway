package org.me.android_experiment_02;

import android.app.Activity;
import android.os.Bundle;

public class Main extends Activity
{
    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);

        // The content of our screen.
        setContentView(R.layout.main);
    }
}
