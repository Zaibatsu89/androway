package androway.ui.quick_action;

import android.content.Context;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.ViewGroup;

import java.util.ArrayList;
import androway.ui.R;

/**
 * Popup window, shows action list as icon and text (QuickContact / Twitter app). 
 * 
 * @author Lorensius. W. T
 */
public class QuickAction extends CustomPopupWindow {
	private final View _ROOT;
	private final ImageView _M_ARROW_UP;
	private final ImageView _M_ARROW_DOWN;
	private final Animation _M_TRACK_ANIM;
	private final LayoutInflater _INFLATER;
	private final Context _CONTEXT;
	
	public static final int ANIM_GROW_FROM_LEFT = 1;
	public static final int ANIM_GROW_FROM_RIGHT = 2;
	public static final int ANIM_GROW_FROM_CENTER = 3;
	public static final int ANIM_AUTO = 4;
	
	private int _animStyle;
	private boolean _animateTrack;
	private ViewGroup _mTrack;
	private ArrayList _actionList;
	
	/**
	 * Constructor
	 * 
	 * @param anchor  {@link View} on where the popup should be displayed
	 */
	public QuickAction(View anchor)
        {
		super(anchor);
		
		_actionList	= new ArrayList();
		_CONTEXT		= anchor.getContext();
		_INFLATER 	= (LayoutInflater) _CONTEXT.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		_ROOT		= (ViewGroup) _INFLATER.inflate(R.layout.quickaction, null);
		
		_M_ARROW_DOWN 	= (ImageView) _ROOT.findViewById(R.id.arrow_down);
		_M_ARROW_UP 	= (ImageView) _ROOT.findViewById(R.id.arrow_up);
		
		setContentView(_ROOT);
		
		_M_TRACK_ANIM 	= AnimationUtils.loadAnimation(anchor.getContext(), R.anim.rail);
		
		_M_TRACK_ANIM.setInterpolator(new Interpolator() {
			public float getInterpolation(float t) {
	              // Pushes past the target area, then snaps back into place.
	                // Equation for graphing: 1.2-((x*1.6)-1.1)^2
				final float inner = (t * 1.55f) - 1.1f;
				
	            return 1.2f - inner * inner;
	        }
		});
	        
		_mTrack 			= (ViewGroup) _ROOT.findViewById(R.id.tracks);
		_animStyle		= ANIM_AUTO;
		_animateTrack	= true;
	}

	/**
	 * Animate track
	 * 
	 * @param _animateTrack flag to animate track
	 */
	public void animateTrack(boolean animateTrack) {
		this._animateTrack = animateTrack;
	}
	
	/**
	 * Set animation style
	 * 
	 * @param _animStyle animation style, default is set to ANIM_AUTO
	 */
	public void setAnimStyle(int animStyle) {
		this._animStyle = animStyle;
	}

	/**
	 * Add action item
	 * 
	 * @param action  {@link ActionItem}
	 */
	public void addActionItem(ActionItem action) {
		_actionList.add(action);
	}
	
	/**
	 * Show popup window
	 */
	public void show () {
		preShow();

		int[] location 		= new int[2];
		
		anchor.getLocationOnScreen(location);

		Rect anchorRect 	= new Rect(location[0], location[1], location[0] + anchor.getWidth(), location[1] 
		                	+ anchor.getHeight());

		_ROOT.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		_ROOT.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		
		int rootWidth 		= _ROOT.getMeasuredWidth();
		int rootHeight 		= _ROOT.getMeasuredHeight();

		int screenWidth 	= windowManager.getDefaultDisplay().getWidth();
		//int screenHeight 	= windowManager.getDefaultDisplay().getHeight();

		int xPos 			= (screenWidth - rootWidth) / 2;
		int yPos	 		= anchorRect.top - rootHeight;

		boolean onTop		= true;
		
		// display on bottom
		if (rootHeight > anchorRect.top) {
			yPos 	= anchorRect.bottom;
			onTop	= false;
		}

		showArrow(((onTop) ? R.id.arrow_down : R.id.arrow_up), anchorRect.centerX());
		
		setAnimationStyle(screenWidth, anchorRect.centerX(), onTop);
		
		createActionList();
		
		window.showAtLocation(this.anchor, Gravity.NO_GRAVITY, xPos, yPos);
		
		if (_animateTrack) _mTrack.startAnimation(_M_TRACK_ANIM);
	}
	
	/**
	 * Set animation style
	 * 
	 * @param screenWidth Screen width
	 * @param requestedX distance from left screen
	 * @param onTop flag to indicate where the popup should be displayed. Set TRUE if displayed on top of anchor and vice versa
	 */
	private void setAnimationStyle(int screenWidth, int requestedX, boolean onTop) {
		int arrowPos = requestedX - _M_ARROW_UP.getMeasuredWidth()/2;

		switch (_animStyle) {
		case ANIM_GROW_FROM_LEFT:
			window.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Left : R.style.Animations_PopDownMenu_Left);
			break;
					
		case ANIM_GROW_FROM_RIGHT:
			window.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Right : R.style.Animations_PopDownMenu_Right);
			break;
					
		case ANIM_GROW_FROM_CENTER:
			window.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Center : R.style.Animations_PopDownMenu_Center);
		break;
					
		case ANIM_AUTO:
			if (arrowPos <= screenWidth/4) {
				window.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Left : R.style.Animations_PopDownMenu_Left);
			} else if (arrowPos > screenWidth/4 && arrowPos < 3 * (screenWidth/4)) {
				window.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Center : R.style.Animations_PopDownMenu_Center);
			} else {
				window.setAnimationStyle((onTop) ? R.style.Animations_PopDownMenu_Right : R.style.Animations_PopDownMenu_Right);
			}
					
			break;
		}
	}
	
	/**
	 * Create action list
	 * 	 
	 */
	private void createActionList() {
		View view;
		String title;
		Drawable icon;
		OnClickListener listener;
		int index = 1;
		
		for (int i = 0; i < _actionList.size(); i++) {
			ActionItem actionItem = (ActionItem)_actionList.get(i);
			title 		= actionItem.getTitle();
			icon 		= actionItem.getIcon();
			listener	= actionItem.getListener();
	
			view 		= getActionItem(title, icon, listener);
		
			view.setFocusable(true);
			view.setClickable(true);
			 
			_mTrack.addView(view, index);
			
			index++;
		}
	}
	
	/**
	 * Get action item {@link View}
	 * 
	 * @param title action item title
	 * @param icon {@link Drawable} action item icon
	 * @param listener {@link View.OnClickListener} action item listener
	 * @return action item {@link View}
	 */
	private View getActionItem(String title, Drawable icon, OnClickListener listener) {
		LinearLayout container	= (LinearLayout) _INFLATER.inflate(R.layout.action_item, null);
		ImageView img 			= (ImageView) container.findViewById(R.id.icon);
		TextView text 			= (TextView) container.findViewById(R.id.title);
		
		if (icon != null) {
			img.setImageDrawable(icon);
		} else {
			img.setVisibility(View.GONE);
		}
		
		if (title != null) {
			text.setText(title);
		} else {
			text.setVisibility(View.GONE);
		}
		
		if (listener != null) {
			container.setOnClickListener(listener);
		}

		return container;
	}
	
	/**
	 * Show arrow
	 * 
	 * @param whichArrow arrow type resource id
	 * @param requestedX distance from left screen
	 */
	private void showArrow(int whichArrow, int requestedX) {
        final View showArrow = (whichArrow == R.id.arrow_up) ? _M_ARROW_UP : _M_ARROW_DOWN;
        final View hideArrow = (whichArrow == R.id.arrow_up) ? _M_ARROW_DOWN : _M_ARROW_UP;

        final int arrowWidth = _M_ARROW_UP.getMeasuredWidth();

        showArrow.setVisibility(View.VISIBLE);
        
        ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams)showArrow.getLayoutParams();
        
        param.leftMargin = requestedX - arrowWidth / 2;
      
        hideArrow.setVisibility(View.INVISIBLE);
    }
}