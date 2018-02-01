package rc.loveq.eye.widget;

import android.content.Context;
import android.util.AttributeSet;

import rc.loveq.eye.utils.ViewUtils;

/**
 * Created by rc on 2018/2/1.
 * Description:
 */

public class CircularImageView extends ForegroundImageView {
    public CircularImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOutlineProvider(ViewUtils.CIRCULAR_OUTLINE);
        setClipToOutline(true);
    }
}
