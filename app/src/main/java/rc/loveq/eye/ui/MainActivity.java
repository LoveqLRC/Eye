package rc.loveq.eye.ui;

import android.app.ActivityManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader;
import com.bumptech.glide.util.ViewPreloadSizeProvider;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.Items;
import me.drakeet.multitype.MultiTypeAdapter;
import rc.loveq.eye.R;
import rc.loveq.eye.Register;
import rc.loveq.eye.data.EyeService;
import rc.loveq.eye.data.RetrofitClient;
import rc.loveq.eye.data.model.Daily;
import rc.loveq.eye.data.model.ItemList;
import rc.loveq.eye.data.model.LoadMore;
import rc.loveq.eye.data.model.TextHeader;
import rc.loveq.eye.ui.adapter.main.VideoViewBinder;
import rc.loveq.eye.ui.base.BaseActivity;
import rc.loveq.eye.utils.AnimUtils;
import rc.loveq.eye.utils.DrawableUtils;
import rc.loveq.eye.utils.NetworkUtils;
import rc.loveq.eye.utils.RxSchedulers;
import rc.loveq.eye.utils.ViewUtils;
import rc.loveq.eye.utils.recyclerview.InfiniteScrollListener;
import rc.loveq.eye.utils.recyclerview.MainItemDividerDecoration;
import retrofit2.HttpException;

public class MainActivity extends BaseActivity {

    @BindView(R.id.main_rv)
    RecyclerView mMainRv;

    @BindView(R.id.drawer)
    DrawerLayout mDrawer;

    public Items mItems;
    public MultiTypeAdapter mAdapter;
    private String dateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mDrawer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        initView(savedInstanceState);
    }

    private void initView(Bundle savedInstanceState) {
        setupToolbar();
        animateToolbar(savedInstanceState);
        setupDrawerLayout();
        setupMainRv();
        setupTaskDescription();
    }

    private void animateToolbar(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            View t = mToolbar.getChildAt(0);
            if (t != null && t instanceof TextView) {
                TextView title = (TextView) t;
                title.setAlpha(0f);
                title.setScaleX(0.8f);
                title.animate()
                        .alpha(1f)
                        .scaleX(1f)
                        .setStartDelay(300)
                        .setDuration(900)
                        .setInterpolator(AnimUtils.getFastOutSlowInInterpolator(this));
            }
        }
    }

    private void setupDrawerLayout() {
        mDrawer.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                ViewGroup.MarginLayoutParams lpToolbar = (ViewGroup.MarginLayoutParams) mToolbar.getLayoutParams();
                lpToolbar.topMargin += insets.getSystemWindowInsetTop();
                lpToolbar.leftMargin += insets.getSystemWindowInsetLeft();
                lpToolbar.rightMargin += insets.getSystemWindowInsetRight();
                mToolbar.setLayoutParams(lpToolbar);
                mMainRv.setPadding(
                        mMainRv.getPaddingLeft() + insets.getSystemWindowInsetLeft(),
                        insets.getSystemWindowInsetTop()
                                + ViewUtils.getActionBarSize(MainActivity.this),
                        mMainRv.getPaddingRight() + insets.getSystemWindowInsetRight(), // landscape
                        mMainRv.getPaddingBottom() + insets.getSystemWindowInsetBottom());

                View statusBarBackground = findViewById(R.id.status_bar_background);
                FrameLayout.LayoutParams lpStatus = (FrameLayout.LayoutParams)
                        statusBarBackground.getLayoutParams();
                lpStatus.height = insets.getSystemWindowInsetTop();
                statusBarBackground.setLayoutParams(lpStatus);

                mDrawer.setOnApplyWindowInsetsListener(null);
                return insets.consumeSystemWindowInsets();
            }
        });
    }

    private void setupMainRv() {
        mItems = new Items();
        mAdapter = new MultiTypeAdapter(mItems);
        Register.registerMainItem(mAdapter, this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        ViewPreloadSizeProvider<ItemList> preloadSizeProvider = new ViewPreloadSizeProvider<>();
        VideoViewBinder videoViewBinder = new VideoViewBinder(this);
        mAdapter.register(ItemList.class, videoViewBinder);

        RecyclerViewPreloader<ItemList> recyclerViewPreloader =
                new RecyclerViewPreloader<>(this,
                        videoViewBinder, preloadSizeProvider, 4);
        mMainRv.addOnScrollListener(recyclerViewPreloader);

        mMainRv.setLayoutManager(layoutManager);
        mMainRv.setAdapter(mAdapter);
        mMainRv.addItemDecoration(new MainItemDividerDecoration(getResources().getDimensionPixelSize(R.dimen.main_item_divider_size)));
        mMainRv.addOnScrollListener(new InfiniteScrollListener(layoutManager) {
            @Override
            protected void onLoadMore(int page) {
                loadData(false, Long.valueOf(dateTime));
            }
        });

    }

    private void setupTaskDescription() {
        Bitmap overviewIcon = DrawableUtils.drawableToBitmap(this, getApplicationInfo().icon);
        setTaskDescription(new ActivityManager.TaskDescription(getString(R.string.app_name),
                overviewIcon,
                ContextCompat.getColor(this, R.color.colorPrimary)));
        overviewIcon.recycle();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        loadData(true, null);
    }

    private void loadData(boolean clear, Long dateTime) {
        EyeService service = RetrofitClient.getEyeService();
        service.getDaily(dateTime)
                .compose(RxSchedulers.flowable_io_main())
                .compose(bindToLifecycle())
                .doOnNext(daily -> {
                    if (clear) mItems.clear();
                    if (mItems.size() > 0) {
                        Object item = mItems.get(mItems.size() - 1);
                        if (item instanceof LoadMore) {
                            mItems.remove(item);
                        }
                    }
                })
                .doAfterTerminate(() -> mAdapter.notifyDataSetChanged())
                .subscribe(this::bindData, this::handleError);
    }

    private void bindData(Daily daily) {
        for (Daily.IssueList list : daily.issueList) {
            for (ItemList itemList : list.itemList) {
                if (TextUtils.equals(itemList.type, "textHeader")) {
                    mItems.add(new TextHeader(itemList.data.text));
                } else if (TextUtils.equals(itemList.type, "banner2")) {
                    mItems.add(new TextHeader("今日开眼精选"));
                } else if (TextUtils.equals(itemList.type, "video")) {
                    mItems.add(itemList);
                } else {
//                    throw new IllegalArgumentException("itemList.type not match");
                }
            }
        }
        mItems.add(new LoadMore());
        String nextPageUrl = daily.nextPageUrl;
        dateTime = nextPageUrl.substring(nextPageUrl.indexOf("=") + 1,
                nextPageUrl.indexOf("&"));
    }

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

//    private RecyclerView.OnScrollListener toolbarElevation = new RecyclerView.OnScrollListener() {
//        @Override
//        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//            if (newState == RecyclerView.SCROLL_STATE_IDLE
//                    && mLayoutManager.findFirstVisibleItemPosition() == 0
//                    && mLayoutManager.findViewByPosition(0).getTop() == mMainRv.getPaddingTop()
//                    && mToolbar.getTranslationZ() != 0) {
//                // at top, reset elevation
//                mToolbar.setTranslationZ(0f);
//            } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING
//                    && mToolbar.getTranslationZ() != -1f) {
//                // grid scrolled, lower toolbar to allow content to pass in front
//                mToolbar.setTranslationZ(-1f);
//            }
//        }
//    };
}
