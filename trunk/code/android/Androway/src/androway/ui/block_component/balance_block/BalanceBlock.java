package androway.ui.block_component.balance_block;

import android.content.Context;
import android.widget.LinearLayout;
import androway.ui.R;
import androway.ui.block_component.BlockComponent;
import java.util.Map;

/**
 * The balance block
 * @author Tymen
 * @since 21-03-2011
 * @version 0.2
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
        if(updateType.equals(BlockComponent.UPDATE_TYPE_TILT))
        {
            // Update the BalanceViewHandler
            _balanceViewHandler.updateView((Float)params.get("speed"), (Float)params.get("direction"));
        }
    }
}
