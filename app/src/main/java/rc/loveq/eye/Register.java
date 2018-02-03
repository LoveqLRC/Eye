package rc.loveq.eye;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.transition.AutoTransition;
import android.view.View;

import me.drakeet.multitype.MultiTypeAdapter;
import rc.loveq.eye.data.model.Data;
import rc.loveq.eye.data.model.LoadMore;
import rc.loveq.eye.data.model.ReplyList;
import rc.loveq.eye.data.model.TextHeader;
import rc.loveq.eye.ui.adapter.LoadMoreViewBinder;
import rc.loveq.eye.ui.adapter.main.TextHeaderViewBinder;
import rc.loveq.eye.ui.adapter.video.VideoCommentsViewBinder;
import rc.loveq.eye.ui.adapter.video.VideoDescriptionViewBinder;
import rc.loveq.eye.utils.recyclerview.CommentAnimator;

/**
 * Author：Rc
 * 0n 2018/1/27 21:20
 */

public class Register {
    public static void registerMainItem(MultiTypeAdapter adapter, Activity activity) {
        adapter.register(TextHeader.class, new TextHeaderViewBinder());
//        adapter.register(ItemList.class, new VideoViewBinder(activity));
        adapter.register(LoadMore.class, new LoadMoreViewBinder());
    }


    public static void registerVideoItem(MultiTypeAdapter adapter, AutoTransition expandCollapse,
                                         RecyclerView rvVideoComments, CommentAnimator animator,
                                         View videoDescription) {
        adapter.register(Data.class, new VideoDescriptionViewBinder(videoDescription));
        adapter.register(ReplyList.class, new VideoCommentsViewBinder(expandCollapse, rvVideoComments, animator));
        adapter.register(LoadMore.class, new LoadMoreViewBinder());
    }
}
