package androway.ui.block_component;

import android.content.Context;
import android.widget.LinearLayout;
import androway.ui.R;
import java.util.Map;

/**
 * The block component parent for all blocks that need to be added to the view flipper
 * @author Tymen
 * @since 17-03-2011
 * @version 0.2
 */
public abstract class BlockComponent extends LinearLayout
{
    public static final String UPDATE_TYPE_TILT = "tilt";    
    public static final int ID_BLOCK_1 = R.id.block1_flipper;
    public static final int ID_BLOCK_2 = R.id.block2_flipper;
    public static String BLOCK_NAME = "noUniqueBlockName";

    public final int blockId;

    private int _layoutId;
    
    public BlockComponent(Context context, int layoutId, int blockType)
    {
        super(context);

        _layoutId = layoutId;
        blockId = blockType;

        this.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 1f));
        inflate(context, _layoutId, this);
    }

    public abstract void updateView(String updateType, Map<String, Object> params);
}