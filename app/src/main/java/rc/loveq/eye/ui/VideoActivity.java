package rc.loveq.eye.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

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
import rc.loveq.eye.utils.RxSchedulers;
import rc.loveq.eye.utils.TransitionUtils;
import rc.loveq.eye.utils.glide.GlideApp;
import rc.loveq.eye.utils.recyclerview.CommentAnimator;
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


    private ItemList mItemList;
    public Items mItems;
    private MultiTypeAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {
        initCover();
        initCommentList();
        chromeFader = new ElasticDragDismissFrameLayout.SystemChromeFader(this) {
            @Override
            public void onDragDismissed() {
                finishAfterTransition();
            }
        };
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
        Register.registerVideoItem(mAdapter, expandCollapse, mRvVideoComments, animator);
        mRvVideoComments.setAdapter(mAdapter);
        mRvVideoComments.setItemAnimator(animator);
    }


    private void initData() {
//        initCommentListData();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        initCommentListData();
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

    private void calculateFabPosition() {

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
}
