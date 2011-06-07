package proj.androway.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.preference.DialogPreference;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.LinearLayout;

/*
 * The following code was written by Matthew Wiggins
 * and is released under the APACHE 2.0 license
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Customized by Tymen Steur
 */
public class SliderPreference extends DialogPreference implements SeekBar.OnSeekBarChangeListener
{
  private static final String androidns = "http://schemas.android.com/apk/res/android";
  private static final String androwayns = "http://schemas.android.com/apk/res/proj.androway";
  
  private SeekBar mSeekBar;
  private TextView mValueText;
  private Context mContext;

  private int additionalTextId;
  private int summaryId;
  private int mDefault, mMax, mValue, mStepSize;
  private boolean _valuesAsFloat;

  public SliderPreference(Context context, AttributeSet attrs)
  {
    super(context, attrs);    
    mContext = context;

    mValue = shouldPersist() ? getPersistedInt(mDefault) : 0;

    // Check if the value needs to be interpreted as a float or not
    String inputTypeValue = attrs.getAttributeValue(androwayns, "valueType");
    _valuesAsFloat = inputTypeValue != null && inputTypeValue.equals("float") ? true : false;

    String textIdString = attrs.getAttributeValue(androidns, "text");
    additionalTextId = Integer.parseInt(textIdString.substring(1, textIdString.length()));
    summaryId = attrs.getAttributeIntValue(androidns, "summary", 0);
    mDefault = attrs.getAttributeIntValue(androidns, "defaultValue", 0);
    mMax = attrs.getAttributeIntValue(androidns,"max", 100);
    mStepSize = (int)Float.parseFloat(attrs.getAttributeValue(androidns, "stepSize"));
  }
  
  @Override
  protected View onCreateDialogView()
  {
    LinearLayout.LayoutParams params;
    LinearLayout layout = new LinearLayout(mContext);
    layout.setOrientation(LinearLayout.VERTICAL);
    layout.setPadding(6,6,6,6);

    if (summaryId != 0)
    {
        TextView mSplashText = new TextView(mContext);
        mSplashText.setText(mContext.getString(summaryId));
        layout.addView(mSplashText);
    }

    mValueText = new TextView(mContext);
    mValueText.setGravity(Gravity.CENTER_HORIZONTAL);
    mValueText.setTextSize(20);
    params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    layout.addView(mValueText, params);

    mSeekBar = new SeekBar(mContext);
    mSeekBar.setOnSeekBarChangeListener(this);
    layout.addView(mSeekBar, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

    if (shouldPersist())
      mValue = getPersistedInt(mDefault);

    mSeekBar.setMax(mMax);
    mSeekBar.setProgress(mValue);
    
    return layout;
  }
  
  @Override
  protected void onBindDialogView(View v)
  {
    super.onBindDialogView(v);
    mSeekBar.setMax(mMax);
    mSeekBar.setProgress(mValue);
  }
  
  @Override
  protected void onSetInitialValue(boolean restore, Object defaultValue)
  {
    super.onSetInitialValue(restore, defaultValue);
    if (restore)
      mValue = shouldPersist() ? getPersistedInt(mDefault) : 0;
    else
      mValue = (Integer)defaultValue;
  }

  public void onProgressChanged(SeekBar seek, int value, boolean fromTouch)
  {
    String t = "";

    // Round the value to the closest integer value
    if (mStepSize >= 1)
        value = Math.round(value/mStepSize) * mStepSize;

    if(_valuesAsFloat)
        t = String.valueOf((float)value / 100f);
    else
        t = String.valueOf(value);

    mValueText.setText(additionalTextId == 0 ? t : t.concat(mContext.getString(additionalTextId)));
    
    if (shouldPersist())
      persistInt(value);

    callChangeListener(new Integer(value));
  }
  
  public void onStartTrackingTouch(SeekBar seek) {}
  public void onStopTrackingTouch(SeekBar seek) {}

  public void setMax(int max) { mMax = max; }
  public int getMax() { return mMax; }

  public void setProgress(int progress) {
    mValue = progress;
    if (mSeekBar != null)
      mSeekBar.setProgress(progress);
  }
  public int getProgress() { return mValue; }
}

