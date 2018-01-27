package rc.loveq.eye.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import rc.loveq.eye.R;
import rc.loveq.eye.data.EyeService;
import rc.loveq.eye.data.RetrofitClient;
import rc.loveq.eye.data.model.Daily;
import rc.loveq.eye.ui.base.BaseActivity;
import rc.loveq.eye.utils.RxSchedulers;

public class MainActivity extends BaseActivity {

    @BindView(R.id.main_rv)
    RecyclerView mMainRv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setupToolbar();
        initMainRv();
    }

    private void initMainRv() {
        EyeService service = RetrofitClient.getEyeService();
        service.getDaily()
                .compose(RxSchedulers.flowable_io_main())
                .compose(bindToLifecycle())
                .doOnNext(new Consumer<Daily>() {
                    @Override
                    public void accept(Daily daily) throws Exception {
                        Log.d("MainActivity", "doOnNext");
                    }
                })
                .doAfterTerminate(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.d("MainActivity", "doAfterTerminate");
                    }
                })
                .subscribe(new Consumer<Daily>() {
                    @Override
                    public void accept(Daily daily) throws Exception {
                        Log.d("MainActivity", "subscribe");

                    }
                });

        mMainRv.setLayoutManager(new LinearLayoutManager(this));
    }
}
