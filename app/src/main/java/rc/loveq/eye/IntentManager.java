package rc.loveq.eye;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.SharedElementCallback;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;

import java.util.List;
import java.util.Map;

import rc.loveq.eye.data.model.ItemList;
import rc.loveq.eye.ui.AuthorActivity;
import rc.loveq.eye.ui.VideoActivity;

/**
 * Author：Rc
 * 0n 2018/1/28 17:31
 */

public class IntentManager {
    public static void startVideoActivity(View view, AppCompatActivity activity, ItemList item) {
        Intent intent = new Intent(activity, VideoActivity.class);
        intent.putExtra(VideoActivity.EXTRA_VIDEO, item);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(activity,
                Pair.create(view, activity.getString(R.string.transition_video)),
                Pair.create(view, activity.getString(R.string.transition_video_background)));
        activity.startActivity(intent, options.toBundle());
        activity.setExitSharedElementCallback(createSharedElementReenterCallback(activity));
    }

    public static void startAuthorActivity(View parent, View avatar, AppCompatActivity activity) {
        Intent intent = new Intent(activity, AuthorActivity.class);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(activity,
                Pair.create(parent, activity.getString(R.string.transition_author_background)),
                Pair.create(avatar, activity.getString(R.string.transition_author_avatar)));
        activity.startActivity(intent, options.toBundle());

    }

    private static SharedElementCallback createSharedElementReenterCallback(@NonNull Context context) {
        final String shotTransitionName = context.getString(R.string.transition_video);
        final String shotBackgroundTransitionName =
                context.getString(R.string.transition_video_background);
        return new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                if (sharedElements.size() != names.size()) {
                    // couldn't map all shared elements
                    final View sharedShot = sharedElements.get(shotTransitionName);
                    if (sharedShot != null) {
                        // has shot so add shot background, mapped to same view
                        sharedElements.put(shotBackgroundTransitionName, sharedShot);
                    }
                }
            }
        };
    }
}
