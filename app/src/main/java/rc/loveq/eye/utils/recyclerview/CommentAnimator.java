package rc.loveq.eye.utils.recyclerview;

import android.support.v7.widget.RecyclerView;

/**
 * Created by rc on 2018/2/1.
 * Description:
 */

public class CommentAnimator extends SlideInItemAnimator {
    private boolean animateMoves = false;

    public CommentAnimator() {
        super();
    }

   public void setAnimateMoves(boolean animateMoves) {
        this.animateMoves = animateMoves;
    }

    @Override
    public boolean animateMove(
            RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY) {
        if (!animateMoves) {
            dispatchMoveFinished(holder);
            return false;
        }
        return super.animateMove(holder, fromX, fromY, toX, toY);
    }
}
