package rc.loveq.eye.data.interceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import rc.loveq.eye.App;
import rc.loveq.eye.utils.NetworkUtils;


/**
 * Author：Rc
 * 0n 2018/1/16 22:05
 */

public class HttpCacheInterceptor implements Interceptor {
    // 设缓存有效期为两天
    private static final long CACHE_STALE_SEC = 60 * 60 * 24 * 2;
    // 30秒内直接读缓存
    private static final long CACHE_AGE_SEC = 0;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (NetworkUtils.isNetworkConnected(App.getsApplicationContext())) {
            request = request.newBuilder().removeHeader("Pragma").removeHeader("Cache-Control").header("Cache-Control", "public, max-age=" + CACHE_AGE_SEC).build();
            Response response = chain.proceed(request);
            return response.newBuilder().removeHeader("Pragma").removeHeader("Cache-Control").header("Cache-Control", "public, max-age=" + CACHE_AGE_SEC).build();
        } else {
            // 无网情况下CACHE_STALE_SEC秒内读取缓存，大于CACHE_STALE_SEC秒缓存无效报504
            request = request.newBuilder().removeHeader("Pragma").removeHeader("Cache-Control")
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + CACHE_STALE_SEC).build();
            Response response = chain.proceed(request);
            return response.newBuilder().removeHeader("Pragma").removeHeader("Cache-Control")
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + CACHE_STALE_SEC).build();
        }
    }
}
