package org.me.android_experiment_01;

import android.app.Activity;
import android.app.WallpaperManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;

/**
 *
 * @author Rinse
 */
public class Androway extends Activity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        // ToDo add your GUI initialization code here
		// See res/layout/wallpaper_2.xml for this
        // view layout definition, which is being set here as
        // the content of our screen.
        setContentView(R.layout.main);
        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        final Drawable wallpaperDrawable = wallpaperManager.getDrawable();
        wallpaperDrawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);

        final ImageView imageView = (ImageView) findViewById(R.id.imageview);
		imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.setDrawingCacheEnabled(true);
        imageView.setImageDrawable(wallpaperDrawable);
    }
}