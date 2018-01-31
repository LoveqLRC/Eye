package rc.loveq.eye;

import android.app.Activity;

import me.drakeet.multitype.MultiTypeAdapter;
import rc.loveq.eye.data.model.ItemList;
import rc.loveq.eye.data.model.LoadMore;
import rc.loveq.eye.data.model.ReplyList;
import rc.loveq.eye.data.model.TextHeader;
import rc.loveq.eye.ui.adapter.LoadMoreViewBinder;
import rc.loveq.eye.ui.adapter.main.TextHeaderViewBinder;
import rc.loveq.eye.ui.adapter.main.VideoViewBinder;
import rc.loveq.eye.ui.adapter.video.VideoCommentsViewBinder;

/**
 * Author：Rc
 * 0n 2018/1/27 21:20
 */

public class Register {
    public static void registerMainItem(MultiTypeAdapter adapter, Activity activity) {
        adapter.register(TextHeader.class, new TextHeaderViewBinder());
        adapter.register(ItemList.class, new VideoViewBinder(activity));
        adapter.register(LoadMore.class, new LoadMoreViewBinder());
    }


    public static void registerVideoItem(MultiTypeAdapter adapter, Activity activity, long expandDuration) {
        adapter.register(ReplyList.class, new VideoCommentsViewBinder(expandDuration,activity));
        adapter.register(LoadMore.class, new LoadMoreViewBinder());
    }
}
