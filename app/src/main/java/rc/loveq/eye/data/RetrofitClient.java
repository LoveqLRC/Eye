package rc.loveq.eye.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import rc.loveq.eye.App;
import rc.loveq.eye.data.interceptor.HttpCacheInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Author：Rc
 * 0n 2018/1/27 21:22
 */

public class RetrofitClient {
    private Retrofit mRetrofit;
    private HttpCacheInterceptor mCacheInterceptor = new HttpCacheInterceptor();
    private EyeService mEyeService;

    public RetrofitClient() {
        File file = new File(App.getsApplicationContext().getCacheDir(), "cache");
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        Cache cache = new Cache(file, 1024 * 1024 * 100);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cache(cache)
                .addNetworkInterceptor(mCacheInterceptor)
                .addInterceptor(mCacheInterceptor)
                .addInterceptor(httpLoggingInterceptor)
                .retryOnConnectionFailure(true)
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").serializeNulls().create();

        mRetrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(EyeService.EYE_END_POINT)
                .build();
        mEyeService = mRetrofit.create(EyeService.class);
    }

    private static class SingletonHolder {
        private static final RetrofitClient INSTANCE = new RetrofitClient();
    }

    public static EyeService getEyeService() {
        return SingletonHolder.INSTANCE.mEyeService;
    }

}
