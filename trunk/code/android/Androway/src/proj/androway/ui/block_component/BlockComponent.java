package proj.androway.ui.block_component;

import android.content.Context;
import android.widget.LinearLayout;
import java.util.Map;
import proj.androway.R;
import proj.androway.common.SharedObjects;

/**
 * The block component parent for all blocks that need to be added to the view flipper
 * @author Tymen
 * @since 17-03-2011
 * @version 0.2
 */
public abstract class BlockComponent extends LinearLayout
{
    public static final String UPDATE_TYPE_TILT = "tilt";
    public static final String UPDATE_TYPE_SESSION_DATA = "sessionData";
    public static final int ID_BLOCK_1 = R.id.block1_flipper;
    public static final int ID_BLOCK_2 = R.id.block2_flipper;

    public final int blockId;

    private int _layoutId;
    protected SharedObjects _sharedObjects;
    
    public BlockComponent(Context context, SharedObjects sharedObjects, int layoutId, int blockType)
    {
        super(context);

        _sharedObjects = sharedObjects;
        _layoutId = layoutId;
        blockId = blockType;

        this.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 1f));
        inflate(context, _layoutId, this);
    }

    public abstract void updateView(String updateType, Map<String, Object> params);
}