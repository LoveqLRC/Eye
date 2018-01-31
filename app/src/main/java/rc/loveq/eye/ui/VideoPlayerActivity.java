package rc.loveq.eye.ui;

import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import butterknife.BindView;
import butterknife.ButterKnife;
import rc.loveq.eye.R;
import rc.loveq.eye.data.model.ItemList;
import rc.loveq.eye.ui.base.BaseActivity;
import rc.loveq.eye.utils.glide.GlideApp;
import rc.loveq.eye.widget.ParallaxScrimageView;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class VideoPlayerActivity extends BaseActivity {
    public static final String EXTRA_VIDEO = "EXTRA_VIDEO";

    @BindView(R.id.ps_video_cover)
    ParallaxScrimageView mPsVideoCover;
    private ItemList mItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_VIDEO)) {
            mItem = intent.getParcelableExtra(EXTRA_VIDEO);
            bindCover();
        }
    }

    private void bindCover() {
        postponeEnterTransition();
        GlideApp.with(this)
                .load(mItem.data.cover.detail)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .priority(Priority.IMMEDIATE)
                .centerCrop()
                .override(800, 600)
                .transition(withCrossFade())
                .into(mPsVideoCover);
        mPsVideoCover.getViewTreeObserver().addOnPreDrawListener(() -> {
            startPostponedEnterTransition();
            return true;
        });
    }

    @Override
    public void onBackPressed() {
        finishAfterTransition();

    }
}
