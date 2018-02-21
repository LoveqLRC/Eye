package rc.loveq.eye.ui;

import android.annotation.TargetApi;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rc.loveq.eye.R;
import rc.loveq.eye.data.model.SwitchVideoModel;
import rc.loveq.eye.ui.base.BaseActivity;
import rc.loveq.eye.widget.EyeVideoPlayer;

public class EyeVideoPlayerActivity extends BaseActivity {

    @BindView(R.id.video_player)
    EyeVideoPlayer mVideoPlayer;
    public OrientationUtils mOrientationUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eye_video_player);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        initEyeVideoPlayer();
    }

    private void initEyeVideoPlayer() {
        String source1 = "http://baobab.kaiyanapp.com/api/v1/playUrl?vid=27916&editionType=normal&source=aliyun";
        String name = "标清";
        String source2 = "http://baobab.kaiyanapp.com/api/v1/playUrl?vid=27916&editionType=high&source=aliyun";
        String name2 = "高清";

        SwitchVideoModel switchVideoModel = new SwitchVideoModel(name, source1);

        SwitchVideoModel switchVideoModel2 = new SwitchVideoModel(name2, source2);

        List<SwitchVideoModel> list = new ArrayList<>();
        list.add(switchVideoModel);
        list.add(switchVideoModel2);
        mVideoPlayer.setUp(list, true, "测试视频");
        mVideoPlayer.getTitleTextView().setVisibility(View.VISIBLE);

        //设置返回键
        mVideoPlayer.getBackButton().setVisibility(View.VISIBLE);
        //设置旋转
        mOrientationUtils = new OrientationUtils(this, mVideoPlayer);
        //设置全屏按键功能,这是使用的是选择屏幕，而不是全屏
        mVideoPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOrientationUtils.resolveByClick();
            }
        });
        //是否可以滑动调整
        mVideoPlayer.setIsTouchWiget(true);
        //设置返回按键功能
        mVideoPlayer.getBackButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mVideoPlayer.startPlayLogic();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVideoPlayer.onVideoPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVideoPlayer.onVideoResume();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mOrientationUtils != null)
            mOrientationUtils.releaseListener();
    }


    @Override
    public void onBackPressed() {
        //先返回正常状态
        if (mOrientationUtils.getScreenType() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            mVideoPlayer.getFullscreenButton().performClick();
            return;
        }
        //释放所有
        mVideoPlayer.setVideoAllCallBack(null);
        GSYVideoManager.releaseAllVideos();
        super.onBackPressed();
    }
}
