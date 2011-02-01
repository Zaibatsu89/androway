package org.me.android_experiment_01;

import java.io.IOException;
import android.app.Activity;
import android.app.WallpaperManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

/**
 * <h3>SetWallpaper Activity</h3>
 *
 * <p>This demonstrates the how to write an activity that gets the current system wallpaper,
 * modifies it and sets the modified bitmap as system wallpaper.</p>
 */
public class Androway extends Activity {
   
    /**
     * Initialization of the Activity after it is first created.  Must at least
     * call {@link android.app.Activity#setContentView setContentView()} to
     * describe what is to be displayed in the screen.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Be sure to call the super class.
        super.onCreate(savedInstanceState);
        // See res/layout/wallpaper_2.xml for this
        // view layout definition, which is being set here as
        // the content of our screen.
        setContentView(R.layout.wallpaper);
        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        final Drawable wallpaperDrawable = wallpaperManager.getDrawable();
        wallpaperDrawable.setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);

        final ImageView imageView = (ImageView) findViewById(R.id.imageview);
        imageView.setDrawingCacheEnabled(true);
        imageView.setImageDrawable(wallpaperDrawable);

        Button randomize = (Button) findViewById(R.id.randomize);
        randomize.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                wallpaperDrawable.setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
                imageView.setImageDrawable(wallpaperDrawable);
                imageView.invalidate();
            }
        });

        Button setWallpaper = (Button) findViewById(R.id.setwallpaper);
        setWallpaper.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                try {
                    wallpaperManager.setBitmap(imageView.getDrawingCache());
                    finish();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}