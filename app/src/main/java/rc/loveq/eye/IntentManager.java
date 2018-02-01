package rc.loveq.eye;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.util.Pair;
import android.view.View;

import rc.loveq.eye.data.model.ItemList;
import rc.loveq.eye.ui.VideoActivity;

/**
 * Author：Rc
 * 0n 2018/1/28 17:31
 */

public class IntentManager {
    public static void startVideoPlayerActivity(View view, Activity activity, ItemList item) {
        Intent intent = new Intent(activity, VideoActivity.class);
        intent.putExtra(VideoActivity.EXTRA_VIDEO, item);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(activity,
                Pair.create(view, activity.getString(R.string.transition_video)),
                Pair.create(view, activity.getString(R.string.transition_video_background)));
        activity.startActivity(intent, options.toBundle());
    }
}
