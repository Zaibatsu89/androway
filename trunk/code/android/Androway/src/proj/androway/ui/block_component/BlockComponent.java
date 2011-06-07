package proj.androway.ui.block_component;

import android.content.Context;
import android.widget.LinearLayout;
import java.util.Map;
import proj.androway.R;
import proj.androway.common.SharedObjects;

/**
 * The BlockComponent is the parent for all blocks that need to be added to the view flipper
 * @author Rinse Cramer & Tymen Steur
 * @since 06-06-2011
 * @version 0.5
 */
public abstract class BlockComponent extends LinearLayout
{
    /**
     * The update-type for tilt-data based views
     */
    public static final String UPDATE_TYPE_TILT = "tilt";

    /**
     * The update-type for session-data based views
     */
    public static final String UPDATE_TYPE_SESSION_DATA = "sessionData";

    /**
     * The first ViewFlipper block
     */
    public static final int ID_BLOCK_1 = R.id.block1_flipper;

    /**
     * The second ViewFlipper block
     */
    public static final int ID_BLOCK_2 = R.id.block2_flipper;

    /**
     * The block id of this block
     */
    public final int blockId;

    private int _layoutId;
    protected SharedObjects _sharedObjects;

    /**
     * The constructor for the BlockComponent
     * @param context       The application context
     * @param sharedObjects An instance of the common SharedObjects object
     * @param layoutId      The layout id for the compass block
     */
    public BlockComponent(Context context, SharedObjects sharedObjects, int layoutId, int blockType)
    {
        super(context);

        _sharedObjects = sharedObjects;
        _layoutId = layoutId;
        blockId = blockType;

        this.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 1f));
        inflate(context, _layoutId, this);
    }

    /**
     * Update the view with the new data
     * @param updateType    The update type (UPDATE_TYPE_'update type')
     * @param params        The parameters (new data)
     */
    public abstract void updateView(String updateType, Map<String, Object> params);
}