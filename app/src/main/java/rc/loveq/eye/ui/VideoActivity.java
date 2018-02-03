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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Flowable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import me.drakeet.multitype.Items;
import me.drakeet.multitype.MultiTypeAdapter;
import rc.loveq.eye.R;
import rc.loveq.eye.Register;
import rc.loveq.eye.data.EyeService;
import rc.loveq.eye.data.RetrofitClient;
import rc.loveq.eye.data.model.ItemList;
import rc.loveq.eye.data.model.LoadMore;
import rc.loveq.eye.data.model.ReplyList;
import rc.loveq.eye.ui.base.BaseActivity;
import rc.loveq.eye.utils.AnimUtils;
import rc.loveq.eye.utils.ColorUtils;
import rc.loveq.eye.utils.RxSchedulers;
import rc.loveq.eye.utils.TransitionUtils;
import rc.loveq.eye.utils.ViewUtils;
import rc.loveq.eye.utils.glide.GlideApp;
import rc.loveq.eye.utils.glide.GlideUtils;
import rc.loveq.eye.utils.recyclerview.CommentAnimator;
import rc.loveq.eye.widget.CircularImageView;
import rc.loveq.eye.widget.ElasticDragDismissFrameLayout;
import rc.loveq.eye.widget.FABToggle;
import rc.loveq.eye.widget.ParallaxScrimageView;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class VideoActivity extends BaseActivity {
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
    private ItemList mItemList;
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
            mItemList = intent.getParcelableExtra(EXTRA_VIDEO);
            bindCover();
        } else {
            throw new IllegalArgumentException("you should pass ItemList");
        }
    }

    private void bindCover() {
        postponeEnterTransition();
        GlideApp.with(this)
                .load(mItemList.data.cover.detail)
                .listener(mDrawableRequestListener)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .priority(Priority.IMMEDIATE)
                .centerCrop()
                .override(800, 600)
                .transition(withCrossFade())
                .into(mPsVideoCover);
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

        mVideoTitle.setText(mItemList.data.title);
        mVideoDescription.setText(mItemList.data.description);

        String iconUrl;
        String playerName;
        if (mItemList.data.author != null) {
            iconUrl = mItemList.data.author.icon;
            playerName = mItemList.data.author.name;
        } else {
            iconUrl = mItemList.data.provider.icon;
            playerName = mItemList.data.provider.name;
        }
        mPlayerName.setText(playerName);
        TimeZone tz = TimeZone.getTimeZone("UTC");
        SimpleDateFormat df = new SimpleDateFormat("mm:ss");
        df.setTimeZone(tz);
        String duration = df.format(new Date(mItemList.data.duration * 1000));
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
        initCommentListData();
    }


    private void calculateFabPosition() {
        fabOffset = mPsVideoCover.getHeight() + mVideoTitle.getHeight() -
                (mFabHeart.getHeight() / 2);
        mFabHeart.setOffset(fabOffset);
        mFabHeart.setMinOffset(mPsVideoCover.getMinimumHeight() - (mFabHeart.getHeight() / 2));
    }

    private void initCommentListData() {
        EyeService service = RetrofitClient.getEyeService();
        service.getReplies(mItemList.data.id)
                .flatMap(replies -> Flowable.fromIterable(replies.replyList))
                .compose(RxSchedulers.flowable_io_main())
                .compose(bindToLifecycle())
                .doAfterTerminate(new Action() {
                    @Override
                    public void run() throws Exception {
                        mItems.add(0, mItemList.data);
                        mItems.add(new LoadMore());
                        mAdapter.notifyDataSetChanged();
                    }
                })
                .subscribe(new Consumer<ReplyList>() {
                    @Override
                    public void accept(ReplyList list) throws Exception {
                        mItems.add(list);
                    }
                });


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
                    VideoActivity.this.getResources().getDisplayMetrics());
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
                                mBack.setColorFilter(ContextCompat.getColor(VideoActivity.this,
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
                                statusBarColorAnim.addUpdateListener(new ValueAnimator
                                        .AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator animation) {
                                        getWindow().setStatusBarColor(
                                                (int) animation.getAnimatedValue());
                                    }
                                });
                                statusBarColorAnim.setDuration(1000L);
                                statusBarColorAnim.setInterpolator(AnimUtils.
                                        getFastOutSlowInInterpolator(VideoActivity.this));
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
                                            ContextCompat.getColor(VideoActivity.this, R.color.mid_grey),
                                            true
                                    )
                            );
                            mPsVideoCover.setForeground(
                                    ViewUtils.createRipple(palette, 0.3f, 0.6f,
                                            ContextCompat.getColor(VideoActivity.this, R.color.mid_grey),
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

}
