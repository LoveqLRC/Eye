package rc.loveq.eye.ui;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.reactivestreams.Publisher;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import me.drakeet.multitype.Items;
import me.drakeet.multitype.MultiTypeAdapter;
import rc.loveq.eye.IntentManager;
import rc.loveq.eye.R;
import rc.loveq.eye.Register;
import rc.loveq.eye.data.EyeService;
import rc.loveq.eye.data.RetrofitClient;
import rc.loveq.eye.data.model.ItemList;
import rc.loveq.eye.data.model.Replies;
import rc.loveq.eye.ui.base.BaseActivity;
import rc.loveq.eye.utils.AnimUtils;
import rc.loveq.eye.utils.ColorUtils;
import rc.loveq.eye.utils.NetworkUtils;
import rc.loveq.eye.utils.RxSchedulersUtils;
import rc.loveq.eye.utils.TransitionUtils;
import rc.loveq.eye.utils.ViewUtils;
import rc.loveq.eye.utils.glide.GlideApp;
import rc.loveq.eye.utils.glide.GlideUtils;
import rc.loveq.eye.utils.recyclerview.CommentAnimator;
import rc.loveq.eye.widget.CircularImageView;
import rc.loveq.eye.widget.ElasticDragDismissFrameLayout;
import rc.loveq.eye.widget.FABToggle;
import rc.loveq.eye.widget.ParallaxScrimageView;
import retrofit2.HttpException;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class VideoDetailActivity extends BaseActivity {
    public static final String EXTRA_VIDEO = "EXTRA_VIDEO";

    @BindView(R.id.ps_video_cover)
    ParallaxScrimageView mPsVideoCover;
    @BindView(R.id.rv_video_comments)
    RecyclerView mRvVideoComments;
    @BindView(R.id.fab_heart)
    FABToggle mFabHeart;
    @BindView(R.id.draggable_frame)
    ElasticDragDismissFrameLayout mDraggableFrame;
    @BindView(R.id.back)
    ImageButton mBack;

    private ElasticDragDismissFrameLayout.SystemChromeFader chromeFader;

    private static final float SCRIM_ADJUSTMENT = 0.075f;
    private ItemList itemData;
    public Items mItems;
    private MultiTypeAdapter mAdapter;
    private View mShotSpacer;
    private TextView mVideoTitle;
    private TextView mVideoDescription;
    private Button mVideoLikeCount;
    private Button mVideoViewCount;
    private Button mVideoShareCount;
    private TextView mPlayerName;
    private CircularImageView mPlayerAvatar;
    private TextView mShotTimeDuration;
    private View mDescription;
    int fabOffset;
    @BindDimen(R.dimen.large_avatar_size)
    int largeAvatarSize;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        initCover();
        initVideoDescription();
        initCommentList();
        chromeFader = new ElasticDragDismissFrameLayout.SystemChromeFader(this) {
            @Override
            public void onDragDismissed() {
                finishAfterTransition();
            }
        };
    }

    private void initCover() {
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_VIDEO)) {
            itemData = intent.getParcelableExtra(EXTRA_VIDEO);
            bindCover();
        } else {
            throw new IllegalArgumentException("you should pass ItemList");
        }
    }

    private void bindCover() {
        GlideApp.with(this)
                .load(itemData.data.cover.detail)
                .listener(mDrawableRequestListener)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .priority(Priority.IMMEDIATE)
                .centerCrop()
                .override(400, 300)
                .transition(withCrossFade())
                .into(mPsVideoCover);
        postponeEnterTransition();

        mPsVideoCover.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mPsVideoCover.getViewTreeObserver().removeOnPreDrawListener(this);
                calculateFabPosition();
                startPostponedEnterTransition();
                return true;
            }
        });
    }

    private void initVideoDescription() {
        mDescription = getLayoutInflater().inflate(R.layout.layout_video_description,
                mRvVideoComments, false);
        mShotSpacer = mDescription.findViewById(R.id.shot_spacer);
        mVideoTitle = mDescription.findViewById(R.id.tv_video_title);
        mVideoDescription = mDescription.findViewById(R.id.tv_video_description);
        mVideoLikeCount = mDescription.findViewById(R.id.shot_like_count);
        mVideoViewCount = mDescription.findViewById(R.id.shot_view_count);
        mVideoShareCount = mDescription.findViewById(R.id.shot_share_action);
        mPlayerName = mDescription.findViewById(R.id.player_name);
        mPlayerAvatar = mDescription.findViewById(R.id.player_avatar);
        mShotTimeDuration = mDescription.findViewById(R.id.shot_time_duration);

        mVideoTitle.setText(itemData.data.title);
        mVideoDescription.setText(itemData.data.description);

        String iconUrl;
        String playerName;
        if (itemData.data.author != null) {
            iconUrl = itemData.data.author.icon;
            playerName = itemData.data.author.name;
        } else {
            iconUrl = itemData.data.provider.icon;
            playerName = itemData.data.provider.name;
        }
        mPlayerName.setText(playerName);
        TimeZone tz = TimeZone.getTimeZone("UTC");
        SimpleDateFormat df = new SimpleDateFormat("mm:ss");
        df.setTimeZone(tz);
        String duration = df.format(new Date(itemData.data.duration * 1000));
        mShotTimeDuration.setText(duration);
        GlideApp.with(this)
                .load(iconUrl)
                .placeholder(R.drawable.shape_avatar_placeholder)
                .error(R.mipmap.ic_launcher)
                .circleCrop()
                .override(largeAvatarSize, largeAvatarSize)
                .transition(withCrossFade())
                .into(mPlayerAvatar);

    }


    private void initCommentList() {
        CommentAnimator animator = new CommentAnimator();
        AutoTransition expandCollapse = new AutoTransition();
        expandCollapse.setDuration(getResources().getInteger(R.integer.comment_expand_collapse_duration));
        expandCollapse.setInterpolator(AnimUtils.getFastOutSlowInInterpolator(this));
        expandCollapse.addListener(new TransitionUtils.TransitionListenerAdapter() {
            @Override
            public void onTransitionStart(Transition transition) {
                mRvVideoComments.setOnTouchListener((v, event) -> true);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                animator.setAnimateMoves(true);
                mRvVideoComments.setOnTouchListener(null);
            }
        });

        mItems = new Items();
        mAdapter = new MultiTypeAdapter(mItems);
        Register.registerVideoItem(mAdapter, expandCollapse, mRvVideoComments,
                animator, mDescription);

        mRvVideoComments.addOnScrollListener(mScrollListener);
        mRvVideoComments.setAdapter(mAdapter);
        mRvVideoComments.setItemAnimator(animator);
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        loadCommentListData();
    }


    private void calculateFabPosition() {
        fabOffset = mPsVideoCover.getHeight() + mVideoTitle.getHeight() -
                (mFabHeart.getHeight() / 2);
        mFabHeart.setOffset(fabOffset);
        mFabHeart.setMinOffset(mPsVideoCover.getMinimumHeight() - (mFabHeart.getHeight() / 2));
    }

    private void loadCommentListData() {
        EyeService service = RetrofitClient.getEyeService();
        service.getReplies(itemData.data.id)
                .flatMap(new Function<Replies, Publisher<Replies>>() {
                    @Override
                    public Publisher<Replies> apply(Replies replies) throws Exception {
                        return service.getReplies(replies.total, itemData.data.id);
                    }
                }).compose(RxSchedulersUtils.flowable_io_main())
                .compose(bindToLifecycle())
                .subscribe(new Consumer<Replies>() {
                    @Override
                    public void accept(Replies replies) throws Exception {
                        mItems.add(0, itemData.data);
                        mItems.addAll(replies.replyList);
                        mAdapter.notifyDataSetChanged();
                    }
                }, this::handleError);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mDraggableFrame.addListener(chromeFader);
    }

    @Override
    protected void onPause() {
        mDraggableFrame.removeListener(chromeFader);
        super.onPause();

    }

    @Override
    public void onBackPressed() {
        finishAfterTransition();
    }

    @OnClick(R.id.back)
    public void onViewClicked() {
        finishAfterTransition();
    }

    private RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            int scrollY = mDescription.getTop();
            mPsVideoCover.setOffset(scrollY);
            mFabHeart.setOffset(fabOffset + scrollY);
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            mPsVideoCover.setImmediatePin(newState == RecyclerView.SCROLL_STATE_SETTLING);
        }
    };

    private RecyclerView.OnFlingListener mFlingListener = new RecyclerView.OnFlingListener() {
        @Override
        public boolean onFling(int velocityX, int velocityY) {
            mPsVideoCover.setImmediatePin(true);
            return false;
        }
    };

    private RequestListener<Drawable> mDrawableRequestListener = new RequestListener<Drawable>() {
        @Override
        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
            Bitmap bitmap = GlideUtils.getBitmap(resource);
            if (bitmap == null) {
                return false;
            }
            int twentyFourDip = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24,
                    VideoDetailActivity.this.getResources().getDisplayMetrics());
            Palette.from(bitmap)
                    .maximumColorCount(3)
                    .clearFilters()
                    .setRegion(0, 0, bitmap.getWidth() - 1, twentyFourDip)
                    .generate(new Palette.PaletteAsyncListener() {
                        @Override
                        public void onGenerated(@NonNull Palette palette) {
                            boolean isDark;
                            @ColorUtils.Lightness int lightness = ColorUtils.isDark(palette);
                            if (lightness == ColorUtils.LIGHTNESS_UNKNOWN) {
                                isDark = ColorUtils.isDark(bitmap, bitmap.getWidth() / 2, 0);
                            } else {
                                isDark = lightness == ColorUtils.IS_DARK;
                            }
                            if (!isDark) {
                                mBack.setColorFilter(ContextCompat.getColor(VideoDetailActivity.this,
                                        R.color.dark_icon));
                            }

                            int statusBarColor = getWindow().getStatusBarColor();

                            Palette.Swatch topColor = ColorUtils.getMostPopulousSwatch(palette);

                            if (topColor != null && (isDark || Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
                                statusBarColor = ColorUtils.scrimify(topColor.getRgb(),
                                        isDark, SCRIM_ADJUSTMENT);
                                if (!isDark && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    ViewUtils.setLightStatusBar(mPsVideoCover);
                                }
                            }

                            if (statusBarColor != getWindow().getStatusBarColor()) {
                                mPsVideoCover.setScrimColor(statusBarColor);
                                ValueAnimator statusBarColorAnim = ValueAnimator.ofArgb(
                                        getWindow().getStatusBarColor(), statusBarColor);
                                statusBarColorAnim.addUpdateListener(animation -> getWindow().setStatusBarColor(
                                        (int) animation.getAnimatedValue()));
                                statusBarColorAnim.setDuration(1000L);
                                statusBarColorAnim.setInterpolator(AnimUtils.
                                        getFastOutSlowInInterpolator(VideoDetailActivity.this));
                                statusBarColorAnim.start();
                            }

                        }
                    });

            Palette.from(bitmap)
                    .clearFilters()
                    .generate(new Palette.PaletteAsyncListener() {
                        @Override
                        public void onGenerated(@NonNull Palette palette) {
                            mShotSpacer.setBackground(
                                    ViewUtils.createRipple(palette, 0.25f, 0.5f,
                                            ContextCompat.getColor(VideoDetailActivity.this, R.color.mid_grey),
                                            true
                                    )
                            );
                            mPsVideoCover.setForeground(
                                    ViewUtils.createRipple(palette, 0.3f, 0.6f,
                                            ContextCompat.getColor(VideoDetailActivity.this, R.color.mid_grey),
                                            true)
                            );
                        }
                    });
            mPsVideoCover.setBackground(null);
            return false;
        }

        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
            return false;
        }


    };

    @OnClick({R.id.back, R.id.fab_heart})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finishAfterTransition();
                break;
            case R.id.fab_heart:
                IntentManager.startEyeVideoPlayerActivity(this, itemData);
                break;
        }
    }

    //TODO:应该有更加好的办法处理网络问题
    private void handleError(Throwable throwable) {
        if (throwable instanceof HttpException) {
            switch (((HttpException) throwable).code()) {
                case 403:

                    break;
                case 504:
                    if (NetworkUtils.isNetworkConnected(this)) {

                    } else {

                    }
                    break;
                default:
                    ((HttpException) throwable).message();
                    break;
            }
        } else if (throwable instanceof UnknownHostException) {

        } else if (throwable instanceof SocketTimeoutException) {

        } else {

        }


    }
}
