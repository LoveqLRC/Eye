package rc.loveq.eye.utils.recyclerview;

import android.graphics.Rect;
import android.support.annotation.Dimension;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by rc on 2018/1/30.
 * Description:
 */

public class MainItemDividerDecoration extends RecyclerView.ItemDecoration {
    private final int dividerSize;

    public MainItemDividerDecoration(@Dimension int dividerSize) {
        this.dividerSize = dividerSize;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(dividerSize, dividerSize, dividerSize, dividerSize);

    }
}
