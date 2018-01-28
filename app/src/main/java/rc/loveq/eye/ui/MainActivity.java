package rc.loveq.eye.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import me.drakeet.multitype.Items;
import me.drakeet.multitype.MultiTypeAdapter;
import rc.loveq.eye.R;
import rc.loveq.eye.Register;
import rc.loveq.eye.data.EyeService;
import rc.loveq.eye.data.RetrofitClient;
import rc.loveq.eye.data.model.Daily;
import rc.loveq.eye.data.model.ItemList;
import rc.loveq.eye.data.model.TextHeader;
import rc.loveq.eye.ui.base.BaseActivity;
import rc.loveq.eye.utils.RxSchedulers;

public class MainActivity extends BaseActivity {

    @BindView(R.id.main_rv)
    RecyclerView mMainRv;
    public Items mItems;
    public MultiTypeAdapter mAdapter;

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
        Register.registerMainItem(mAdapter);

        mMainRv.setLayoutManager(new LinearLayoutManager(this));
        mMainRv.setAdapter(mAdapter);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        loadData();
    }

    private void loadData() {
        EyeService service = RetrofitClient.getEyeService();
        service.getDaily()
                .compose(RxSchedulers.flowable_io_main())
                .compose(bindToLifecycle())
                .doOnNext(new Consumer<Daily>() {
                    @Override
                    public void accept(Daily daily) throws Exception {
                    }
                })
                .doAfterTerminate(new Action() {
                    @Override
                    public void run() throws Exception {
                        mAdapter.notifyDataSetChanged();
                    }
                })
                .subscribe(new Consumer<Daily>() {
                    @Override
                    public void accept(Daily daily) throws Exception {
                        bindData(daily);
                    }
                });
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
    }
}
