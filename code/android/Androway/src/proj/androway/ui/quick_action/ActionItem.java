package proj.androway.ui.quick_action;

import android.graphics.drawable.Drawable;
import android.view.View.OnClickListener;

/**
 * Action item, displayed as menu with _icon and text.
 *
 * @author Lorensius. W. L. T
 *
 */
public class ActionItem
{
    private Drawable _icon;
    private String _title;
    private OnClickListener _listener;

    /**
     * Constructor
     */
    public ActionItem() {}

    /**
     * Constructor
     *
     * @param icon {@link Drawable} action _icon
     */
    public ActionItem(Drawable icon) {
        this._icon = icon;
    }

    /**
     * Set action _title
     *
     * @param title action _title
     */
    public void setTitle(String title) {
        this._title = title;
    }

    /**
     * Get action _title
     *
     * @return action _title
     */
    public String getTitle() {
        return this._title;
    }

    /**
     * Set action _icon
     *
     * @param icon {@link Drawable} action _icon
     */
    public void setIcon(Drawable icon) {
        this._icon = icon;
    }

    /**
     * Get action _icon
     * @return  {@link Drawable} action _icon
     */
    public Drawable getIcon() {
            return this._icon;
    }

    /**
     * Set on click _listener
     *
     * @param _listener on click _listener {@link View.OnClickListener}
     */
    public void setOnClickListener(OnClickListener listener) {
            this._listener = listener;
    }

    /**
     * Get on click _listener
     *
     * @return on click _listener {@link View.OnClickListener}
     */
    public OnClickListener getListener() {
            return this._listener;
    }
}