package proj.androway.ui.block_component.balance_block;

import android.content.Context;
import android.hardware.Sensor;
import android.widget.LinearLayout;
import proj.androway.R;
import proj.androway.ui.block_component.BlockComponent;
import java.util.Map;
import proj.androway.main.TiltControls;

/**
 * The balance block
 * @author Tymen
 * @since 04-04-2011
 * @version 0.21
 */
public class BalanceBlock extends BlockComponent
{
    private BalanceViewHandler _balanceViewHandler;

    public static String BLOCK_NAME = "balance";

    public BalanceBlock(Context context, int layoutId)
    {
        super(context, layoutId, BlockComponent.ID_BLOCK_1);

        // Add the actual BalanceViewHandler to the BalanceBlock
        _balanceViewHandler = new BalanceViewHandler(context);
        LinearLayout balanceWrapper = (LinearLayout) findViewById(R.id.balance);
        balanceWrapper.addView(_balanceViewHandler);
    }

    @Override
    public void updateView(String updateType, Map<String, Object> params)
    {
		int sensorType = (Integer)params.get(TiltControls.UPDATE_SENSOR_TYPE);
        if(updateType.equals(BlockComponent.UPDATE_TYPE_TILT) && sensorType == Sensor.TYPE_ACCELEROMETER)
        {
			// Update the BalanceViewHandler
            _balanceViewHandler.updateView(params);
        }
    }
}