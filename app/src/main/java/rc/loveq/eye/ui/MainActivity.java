package rc.loveq.eye.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

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
import rc.loveq.eye.ui.base.BaseActivity;
import rc.loveq.eye.utils.RxSchedulers;
import rc.loveq.eye.utils.recyclerview.InfiniteScrollListener;
import rc.loveq.eye.utils.recyclerview.MainItemDividerDecoration;

public class MainActivity extends BaseActivity {

    @BindView(R.id.main_rv)
    RecyclerView mMainRv;
    public Items mItems;
    public MultiTypeAdapter mAdapter;
    private String dateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setupToolbar();
        setupMainRv();
    }

    private void setupMainRv() {
        mItems = new Items();
        mAdapter = new MultiTypeAdapter(mItems);
        Register.registerMainItem(mAdapter, this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
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
                .subscribe(daily -> bindData(daily));
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
                    throw new IllegalArgumentException("itemList.type not match");
                }
            }
        }
        mItems.add(new LoadMore());
        String nextPageUrl = daily.nextPageUrl;
        dateTime = nextPageUrl.substring(nextPageUrl.indexOf("=") + 1,
                nextPageUrl.indexOf("&"));
    }
}
