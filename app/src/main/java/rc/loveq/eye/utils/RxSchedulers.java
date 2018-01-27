package rc.loveq.eye.utils;

import io.reactivex.FlowableTransformer;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Author：Rc
 * 0n 2018/1/27 22:17
 */

public class RxSchedulers {
    private RxSchedulers() {
    }

    public static <T> ObservableTransformer<T, T> io_main() {
        return upstream ->
                upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> FlowableTransformer<T, T> flowable_io_main() {
        return upstream ->
                upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
    }
}
