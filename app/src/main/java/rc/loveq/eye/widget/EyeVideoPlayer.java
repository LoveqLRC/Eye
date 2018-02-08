package rc.loveq.eye.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import rc.loveq.eye.R;

/**
 * Created by rc on 2018/2/6.
 * Description:
 */

public class EyeVideoPlayer extends StandardGSYVideoPlayer {

    public EyeVideoPlayer(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public EyeVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public int getLayoutId() {
        if (mIfCurrentIsFullscreen) {
            return R.layout.eye_video_player_land;
        }
        return R.layout.eye_video_player_normal;
    }

    @Override
    protected void updateStartImage() {
        if (mIfCurrentIsFullscreen) {
            if (mStartButton instanceof ImageView) {
                ImageView startButton = (ImageView) mStartButton;
                if (mCurrentState == CURRENT_STATE_PLAYING) {
                    //设置暂停按钮
                } else if (mCurrentState == CURRENT_STATE_ERROR) {
                    //设置播放按钮
                } else {
                    //设置播放按钮
                }
            }
        } else {
            super.updateStartImage();
        }
    }
}
