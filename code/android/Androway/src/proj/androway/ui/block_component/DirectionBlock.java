package proj.androway.ui.block_component;

import android.content.Context;
import java.util.Map;

/**
 * The inclination block
 * @author Tymen
 * @since 17-03-2011
 * @version 0.1
 */
public class DirectionBlock extends BlockComponent
{
    public DirectionBlock(Context context, int layoutId)
    {
        super(context, layoutId, BlockComponent.ID_BLOCK_2);
    }

    @Override
    public void updateView(String updateType, Map<String, Object> params)
    {
        if(updateType.equals(BlockComponent.UPDATE_TYPE_SESSION_DATA))
        {
        }
    }
}