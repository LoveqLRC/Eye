package rc.loveq.eye;

import me.drakeet.multitype.MultiTypeAdapter;
import rc.loveq.eye.data.model.ItemList;
import rc.loveq.eye.data.model.TextHeader;
import rc.loveq.eye.ui.adapter.TextHeaderViewBinder;
import rc.loveq.eye.ui.adapter.VideoViewBinder;

/**
 * Author：Rc
 * 0n 2018/1/27 21:20
 */

public class Register {
    public static void registerMainItem(MultiTypeAdapter adapter) {
        adapter.register(TextHeader.class, new TextHeaderViewBinder());
        adapter.register(ItemList.class, new VideoViewBinder());
    }
}
